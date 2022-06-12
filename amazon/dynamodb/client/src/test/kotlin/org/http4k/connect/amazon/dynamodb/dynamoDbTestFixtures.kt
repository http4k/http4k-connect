package org.http4k.connect.amazon.dynamodb

import org.http4k.connect.amazon.core.model.Base64Blob
import org.http4k.connect.amazon.dynamodb.action.TableDescriptionResponse
import org.http4k.connect.amazon.dynamodb.model.Attribute
import org.http4k.connect.amazon.dynamodb.model.AttributeValue
import org.http4k.connect.amazon.dynamodb.model.BillingMode
import org.http4k.connect.amazon.dynamodb.model.Item
import org.http4k.connect.amazon.dynamodb.model.KeySchema
import org.http4k.connect.amazon.dynamodb.model.TableName
import org.http4k.connect.amazon.dynamodb.model.asAttributeDefinition
import org.http4k.connect.amazon.dynamodb.model.compound
import org.http4k.connect.amazon.dynamodb.model.value
import org.http4k.connect.successValue
import java.util.*

val attrBool = Attribute.boolean().required("theBool")
val attrB = Attribute.base64Blob().required("theBase64Blob")
val attrBS = Attribute.base64Blobs().required("theBase64Blobs")
val attrN = Attribute.int().required("theNum")
val attrNS = Attribute.ints().required("theNums")
val attrL = Attribute.list().required("theList")
val attrM = Attribute.map().required("theMap")
val attrS = Attribute.string().required("theString")
val attrU = Attribute.uuid().value(MyValueType).required("theUuid")
val attrSS = Attribute.strings().required("theStrings")
val attrNL = Attribute.string().optional("theNull")
val attrMissing = Attribute.string().optional("theMissing")

fun TableName.Companion.sample(prefix: String = "http4k-connect", suffix: String = UUID.randomUUID().toString()): TableName {
    return TableName.of("$prefix-$suffix")
}

fun DynamoDb.createTable(
    tableName: TableName,
    hashKey: Attribute<*>,
    rangeKey: Attribute<*>? = null,
): TableDescriptionResponse = this@createBasicTable.createTable(
    TableName = tableName,
    KeySchema = KeySchema.compound(hashKey.name, rangeKey?.name),
    AttributeDefinitions = listOfNotNull(hashKey.asAttributeDefinition(), rangeKey?.asAttributeDefinition()),
    BillingMode = BillingMode.PAY_PER_REQUEST
).successValue()

fun createItem(
    string: String = "string",
    number: Int = 123,
    binary: Base64Blob = Base64Blob.encode("foo")
) = Item(
    attrS of string,
    attrBool of true,
    attrB of binary,
    attrBS of setOf(Base64Blob.encode("bar")),
    attrN of number,
    attrNS of setOf(123, 321),
    attrL of listOf(AttributeValue.List(listOf(AttributeValue.Str("foo"))), AttributeValue.Num(123), AttributeValue.Null()),
    attrM of Item(attrS of "foo", attrBool of false),
    attrSS of setOf("345", "567"),
    attrU of MyValueType(UUID(0, 1)),
    attrNL of null
)

