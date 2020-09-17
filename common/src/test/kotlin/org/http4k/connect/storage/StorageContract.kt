package org.http4k.connect.storage

import com.natpryce.hamkrest.absent
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.present
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.util.UUID

abstract class StorageContract(private val storage: Storage<String>) {
    @Test
    fun `item lifecycle`() {
        val key = UUID.randomUUID().toString()
        val value = UUID.randomUUID().toString()
        assertThat(storage[key], absent())

        // create first time
        assertTrue(storage.create(key, "value"))
        assertThat(storage[key], present(equalTo("value")))

        // update value
        assertTrue(storage.update(key, "value2"))
        assertThat(storage[key], present(equalTo("value2")))

        // put overwrites
        storage[key] = value
        assertThat(storage[key], present(equalTo(value)))

        // create doesn't overwrite
        assertFalse(storage.create(key, UUID.randomUUID().toString()))
        assertThat(storage[key], present(equalTo(value)))

        // remove
        assertTrue(storage.remove(key))
        assertThat(storage[key], absent())
    }

    @Test
    fun `collection operations`() {
        val key1 = UUID.randomUUID().toString()
        val key2 = UUID.randomUUID().toString()
        assertTrue(storage.removeAll())

        storage[key1] = UUID.randomUUID().toString()
        storage[key2] = UUID.randomUUID().toString()

        assertThat(storage.keySet("") { it + it }, equalTo(setOf(key1 + key1, key2 + key2)))
        assertTrue(storage.removeAll())
    }
}

