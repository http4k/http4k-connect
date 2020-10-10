package org.http4k.connect.storage

import org.http4k.format.AutoMarshalling
import org.http4k.format.Jackson
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

/**
 * Database-backed storage implementation. You probably want to use one of the builder functions instead of this
 */
inline fun <reified T : Any> Storage.Companion.Jdbc(
    db: Database,
    tableName: String = T::class.java.simpleName,
    autoMarshalling: AutoMarshalling = Jackson): Storage<T> = object : Storage<T> {

    private val table = StorageTable(tableName)

    override fun get(key: String) = tx {
        table.select { table.key eq key }.firstOrNull()?.let { autoMarshalling.asA<T>(it[table.contents]) }
    }

    override fun set(key: String, data: T) {
        tx {
            when (table.select { table.key eq key }.count()) {
                0L -> table.insert {
                    it[table.key] = key
                    it[contents] = autoMarshalling.asFormatString(data)
                }
                else -> update(key, data)
            }
        }
    }

    override fun create(key: String, data: T) = tx {
        when (table.select { table.key eq key }.count()) {
            0L -> {
                set(key, data)
                true
            }
            else -> false
        }
    }

    override fun update(key: String, data: T): Boolean = tx {
        table.update({ table.key eq key }) {
            it[contents] = autoMarshalling.asFormatString(data)
        } > 0
    }

    override fun remove(key: String) = tx {
        table.deleteAll() > 0
    }

    override fun <T> keySet(keyPrefix: String, decodeFunction: (String) -> T) = tx {
        when {
            keyPrefix.isBlank() -> table.selectAll()
            else -> table.select { table.key like "$keyPrefix%" }
        }.map { decodeFunction(it[table.key]) }.toSet()
    }

    override fun removeAll(keyPrefix: String) = tx {
        when {
            keyPrefix.isBlank() -> table.deleteAll().run { true }
            else -> table.deleteWhere { table.key like "$keyPrefix%" } > 0
        }
    }

    private fun <T> tx(statement: Transaction.() -> T): T = transaction(db) {
        statement()
    }
}

class StorageTable(name: String = "") : IntIdTable(name) {
    val key: Column<String> = varchar("key", 500)
    val contents: Column<String> = text("contents")
    override val primaryKey = PrimaryKey(key)
}
