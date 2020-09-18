package org.http4k.connect.storage

import com.natpryce.hamkrest.absent
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.junit.jupiter.api.Test

class StorageProviderTest {

    @Test
    fun `prepends key with child name`() {
        val baseStorage = Storage.InMemory<String>()
        val provider = StorageProvider(baseStorage)

        baseStorage.create("foo", "bar")
        val childStorage = provider("child")

        childStorage.create("foo", "bob")
        assertThat(baseStorage["childfoo"], equalTo("bob"))
        assertThat(childStorage["foo"], equalTo("bob"))
        assertThat(baseStorage.keySet("", { it }), equalTo(setOf("childfoo", "foo")))
        assertThat(baseStorage["foo"], equalTo("bar"))

        childStorage.update("foo", "bob2")
        assertThat(baseStorage["childfoo"], equalTo("bob2"))
        assertThat(childStorage["foo"], equalTo("bob2"))
        assertThat(baseStorage["foo"], equalTo("bar"))

        childStorage.remove("foo")
        assertThat(baseStorage["childfoo"], absent())
        assertThat(childStorage["foo"], absent())
        assertThat(baseStorage["foo"], equalTo("bar"))
    }
}
