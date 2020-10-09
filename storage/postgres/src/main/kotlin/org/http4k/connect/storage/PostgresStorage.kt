package org.http4k.connect.storage

import org.http4k.core.Credentials
import org.http4k.format.AutoMarshalling
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.transactions.transaction

object Data : Table() {
    val key: Column<String> = varchar("key", 500)
    val json: Column<String> = text("json")

    override val primaryKey = PrimaryKey(key)
}

fun <T> Storage.Companion.Postgres(url: String, name: String, credentials: Credentials, autoMarshalling: AutoMarshalling): Storage<T> = object : Storage<T> {

    private val db = Database.connect(url, user = credentials.user, password = credentials.password)

    init {
        transaction(db) {
            exec("CREATE OR UPDATE TABLE $name (key VARCHAR2(500), json VARCHAR2(4000))") { rs -> }
        }
    }

    override fun get(key: String): T? {
        TODO("Not yet implemented")
    }

    override fun set(key: String, data: T) {
        TODO("Not yet implemented")
    }

    override fun create(key: String, data: T): Boolean {
        TODO("Not yet implemented")
    }

    override fun update(key: String, data: T): Boolean {
        TODO("Not yet implemented")
    }

    override fun remove(key: String): Boolean {
        TODO("Not yet implemented")
    }

    override fun <T> keySet(keyPrefix: String, decodeFunction: (String) -> T): Set<T> {
        TODO("Not yet implemented")
    }

    override fun removeAll(keyPrefix: String) = Data.deleteWhere { Data.key eq keyPrefix } > 0
}
