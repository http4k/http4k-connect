package org.http4k.connect.storage

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column

object Items : IntIdTable() {
    val key: Column<String> = varchar("key", 500)
    val json: Column<String> = text("json")
    override val primaryKey = PrimaryKey(key)
}

class Item(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Item>(Items)

    var key by Items.key
    var json by Items.json
}
