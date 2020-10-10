package org.http4k.connect.storage

import org.http4k.connect.storage.Items.key
import org.http4k.format.AutoMarshalling
import org.http4k.format.Jackson
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

inline fun <reified T : Any> Storage.Companion.Jdbc(db: Database, autoMarshalling: AutoMarshalling = Jackson): Storage<T> = object : Storage<T> {

    override fun get(key: String) = tx {
        Items.select { Items.key eq key }.firstOrNull()?.let { autoMarshalling.asA<T>(it[Items.json]) }
    }


    override fun set(key: String, data: T) {
        tx {
            when (Items.select { Items.key eq key }.count()) {
                0L -> Items.insert {
                    it[Items.key] = key
                    it[json] = autoMarshalling.asFormatString(data)
                }
                else -> update(key, data)
            }
        }
    }

    override fun create(key: String, data: T) = tx {
        when (Items.select { Items.key eq key }.count()) {
            0L -> {
                set(key, data)
                true
            }
            else -> false
        }
    }

    override fun update(key: String, data: T): Boolean = tx {
        Items.update({ Items.key eq key }) {
            it[json] = autoMarshalling.asFormatString(data)
        } > 0
    }

    override fun remove(key: String) = tx {
        Items.deleteAll() > 0
    }

    override fun <T> keySet(keyPrefix: String, decodeFunction: (String) -> T) = tx {
        when {
            keyPrefix.isBlank() -> Items.selectAll()
            else -> Items.select { key like "$keyPrefix%" }
        }.map { decodeFunction(it[key]) }.toSet()
    }

    override fun removeAll(keyPrefix: String) = tx {
        when {
            keyPrefix.isBlank() -> Items.deleteAll().run { true }
            else -> Items.deleteWhere { key like "$keyPrefix%" } > 0
        }
    }

    private fun <T> tx(statement: Transaction.() -> T): T = transaction(db) {
        statement()
    }

}
