package org.http4k.connect.amazon.model

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import dev.forkhandles.values.IntValue
import dev.forkhandles.values.IntValueFactory
import dev.forkhandles.values.StringValue
import dev.forkhandles.values.StringValueFactory
import dev.forkhandles.values.UUIDValue
import dev.forkhandles.values.UUIDValueFactory
import org.http4k.connect.amazon.dynamodb.action.AttributeValue
import org.junit.jupiter.api.Test
import java.util.UUID

class AttributeTest {

    @Test
    fun `can create value from attributes`() {
        assertThat(Attribute.value(MyStringType).required("name").asValue(MyStringType.of("foo")), equalTo(AttributeValue.Str("foo")))
        assertThat(Attribute.value(MyUUIDType).required("name").asValue(MyUUIDType.of(UUID(0,0))), equalTo(AttributeValue.Str("00000000-0000-0000-0000-000000000000")))
    }
}


class MyStringType(value: String) : StringValue(value) {
    companion object : StringValueFactory<MyStringType>(::MyStringType)
}

class MyUUIDType(value: UUID) : UUIDValue(value) {
    companion object : UUIDValueFactory<MyUUIDType>(::MyUUIDType)
}

class MyIntType(value: Int) : IntValue(value) {
    companion object : IntValueFactory<MyIntType>(::MyIntType)
}
