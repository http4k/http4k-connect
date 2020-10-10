package org.http4k.connect.storage

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

class JdbcStorageTest : StorageContract() {
    private val name = "testDb"

    override val storage: Storage<String> by lazy {
        val db = Database.connect("jdbc:h2:mem:$name;DB_CLOSE_DELAY=-1", driver = "org.h2.Driver")
        transaction(db) {
            SchemaUtils.create(StorageTable("String"))
        }

        Storage.Jdbc(db)
    }
}
