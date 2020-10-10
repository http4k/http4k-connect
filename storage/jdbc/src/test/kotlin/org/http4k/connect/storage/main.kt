package org.http4k.connect.storage

import org.http4k.connect.storage.Items.key
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

fun main() {
    val db = Database.connect("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", driver = "org.h2.Driver")

    transaction(db) {
        addLogger(StdOutSqlLogger)
        SchemaUtils.create(Items)
        Item.new {
            key = "asd"
            json = "dsa"
        }
        Item.new {
            key = "asd"
            json = "dsa"
        }

        println(Items.select { key like "a%" }.map { it[key] })
    }
}
