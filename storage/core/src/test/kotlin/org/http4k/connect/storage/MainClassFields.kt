package org.http4k.connect.storage

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import dev.forkhandles.values.IntValue
import dev.forkhandles.values.IntValueFactory
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import kotlin.reflect.KMutableProperty0

class StorageDataContainerTest {

    class MyType private constructor(value: Int) : IntValue(value) {
        companion object : IntValueFactory<MyType>(::MyType)
    }

    class Child(storage: Storage<Any>) : StorageDataContainer(storage) {
        var anotherString by required<String>()
        var grandchild by obj(::GrandChild)
    }

    class GrandChild(storage: Storage<Any>) : StorageDataContainer(storage) {
        var yetAnotherString by required<String>()
    }

    class Parent(storage: Storage<Any>) : StorageDataContainer(storage) {
        var standardField = "foobar"
        var string by required<String>()
        var decimal by required<BigDecimal>()
        var value by required(MyType)
        var subClass by obj(::Child)
    }

    @Test
    fun `set and get object values`() {
        val storage = Storage.InMemory<Any>()
        val it = Parent(storage)
        assertThat(it.standardField, equalTo("foobar"))
        expectSetWorks(it::string, "helloworld")
        expectSetWorks(it::decimal, BigDecimal(1.24))
        expectSetWorks(it::value, MyType.of(123))
        expectSetWorks(it::subClass, Child(storage).apply {
            anotherString = "worldhello"
        })

        println(storage.keySet())
    }

    private fun <T> expectSetWorks(prop: KMutableProperty0<T>, value: T) {
        prop.set(value)
        assertThat(prop.get(), equalTo(value))
    }
}
