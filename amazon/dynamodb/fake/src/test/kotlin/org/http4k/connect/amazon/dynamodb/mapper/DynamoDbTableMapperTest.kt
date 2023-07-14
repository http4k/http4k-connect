package org.http4k.connect.amazon.dynamodb.mapper

import com.natpryce.hamkrest.absent
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.hasSize
import com.natpryce.hamkrest.isEmpty
import org.http4k.connect.amazon.dynamodb.DynamoTable
import org.http4k.connect.amazon.dynamodb.FakeDynamoDb
import org.http4k.connect.amazon.dynamodb.model.*
import org.http4k.connect.storage.InMemory
import org.http4k.connect.storage.Storage
import org.http4k.connect.successValue
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.util.UUID

private val ownerIdAttr = Attribute.uuid().required("ownerId")
private val nameAttr = Attribute.string().required("name")
private val bornAttr = Attribute.localDate().required("born")
private val idAttr = Attribute.uuid().required("id")

private val byOwner = DynamoDbTableMapperSchema.GlobalSecondary(
    indexName = IndexName.of("by-owner"),
    hashKeyAttribute = ownerIdAttr,
    sortKeyAttribute = nameAttr
)

private val byDob = DynamoDbTableMapperSchema.GlobalSecondary(
    indexName = IndexName.of("by-dob"),
    hashKeyAttribute = bornAttr,
    sortKeyAttribute = idAttr
)

class DynamoDbTableMapperTest {

    private val storage: Storage<DynamoTable> = Storage.InMemory()
    private val tableMapper = FakeDynamoDb(storage).client().tableMapper<Cat, UUID, Unit>(
        TableName = TableName.of("cats"),
        hashKeyAttribute = idAttr
    )

    init {
        tableMapper.createTable(byOwner, byDob)
        tableMapper += listOf(toggles, smokie, bandit, kratos, athena)
    }

    private fun table() = storage["cats"]!!

    @Test
    fun `verify cats table`() {
        val tableData = table().table

        assertThat(tableData.TableName, equalTo(TableName.of("cats")))
        assertThat(tableData.KeySchema, equalTo(KeySchema.compound(AttributeName.of("id"))))
        assertThat(
            tableData.AttributeDefinitions?.toSet(),
            equalTo(setOf(
                AttributeDefinition(AttributeName.of("id"), DynamoDataType.S),
                AttributeDefinition(AttributeName.of("ownerId"), DynamoDataType.S),
                AttributeDefinition(AttributeName.of("name"), DynamoDataType.S),
                AttributeDefinition(AttributeName.of("born"), DynamoDataType.S)
            ))
        )
        assertThat(tableData.GlobalSecondaryIndexes.orEmpty(), hasSize(equalTo(2)))
        assertThat(tableData.LocalSecondaryIndexes, absent())
    }

    @Test
    fun `scan table`() {
        assertThat(
            tableMapper.primaryIndex().scan().toSet(),
            equalTo(setOf(toggles, smokie, bandit, kratos, athena))
        )
    }

    @Test
    fun `get item`() {
        assertThat(tableMapper[toggles.id], equalTo(toggles))
    }

    @Test
    fun `get missing item`() {
        assertThat(tableMapper[UUID.randomUUID()], absent())
    }

    @Test
    fun `query for index`() {
        assertThat(
            tableMapper.index(byOwner).query(owner2).toList(),
            equalTo(listOf(bandit, smokie))
        )
    }

    @Test
    fun `query for index - reverse order`() {
        assertThat(
            tableMapper.index(byOwner).query(owner2, scanIndexForward = false).toList(),
            equalTo(listOf(smokie, bandit))
        )
    }

    @Test
    fun `delete item`() {
        tableMapper -= toggles

        assertThat(table().items, hasSize(equalTo(4)))
    }

    @Test
    fun `delete missing item`() {
        tableMapper.delete(UUID.randomUUID())

        assertThat(table().items, hasSize(equalTo(5)))
    }

    @Test
    fun `delete batch`() {
        tableMapper -= listOf(smokie, bandit)

        assertThat(table().items, hasSize(equalTo(3)))
    }

    @Test
    fun `delete batch by ids`() {
        tableMapper.batchDelete(smokie.id, bandit.id)

        assertThat(table().items, hasSize(equalTo(3)))
    }

    @Test
    fun `delete batch by keys`() {
        tableMapper.batchDelete(listOf(smokie.id to null, bandit.id to null))

        assertThat(table().items, hasSize(equalTo(3)))
    }

    @Test
    fun `delete table`() {
        tableMapper.deleteTable().successValue()
        assertThat(storage["cats"], absent())
    }

    @Test
    fun `custom query`() {
        val results = tableMapper.index(byDob).query(
            filter = "$bornAttr = :val1",
            values = mapOf(":val1" to bornAttr.asValue(smokie.born))
        ).toList()

        assertThat(results, equalTo(listOf(smokie, bandit)))
    }

    @Test
    fun `query page`() {
        // page 1 of 2
        assertThat(tableMapper.index(byOwner).queryPage(owner1, limit = 2), equalTo(DynamoDbPage(
            items = listOf(athena, kratos),
            nextHashKey = owner1,
            nextSortKey = kratos.name
        )))

        // page 2 of 2
        assertThat(tableMapper.index(byOwner).queryPage(owner1, limit = 2, exclusiveStartKey = kratos.name), equalTo(DynamoDbPage(
            items = listOf(toggles),
            nextHashKey = null,
            nextSortKey = null
        )))
    }

    @Test
    fun `scan page`() {
        // page 1 of 1
        assertThat(tableMapper.index(byOwner).scanPage(limit = 3), equalTo(DynamoDbPage(
            items = listOf(bandit, smokie, athena),
            nextHashKey = owner1,
            nextSortKey = athena.name
        )))

        // page 2 of 2
        assertThat(tableMapper.index(byOwner).scanPage(limit = 3, exclusiveStartKey = owner1 to athena.name), equalTo(DynamoDbPage(
            items = listOf(kratos, toggles),
            nextHashKey = null,
            nextSortKey = null
        )))
    }

    @Test
    fun `get empty batch`() {
        val batchGetResult = tableMapper.batchGet(emptyList()).toList()
        assertThat(batchGetResult, isEmpty)
    }

    @Test
    fun `get batch`() {
        val cats = (1..150).map { index ->
            Cat(
                ownerId = UUID.randomUUID(),
                id = UUID.randomUUID(),
                name = "cat$index",
                born = LocalDate.EPOCH
            )
        }

        tableMapper += cats

        val batchGetResult = tableMapper[cats.map { it.id }].toList()
        assertThat(batchGetResult, equalTo(cats))
    }
}
