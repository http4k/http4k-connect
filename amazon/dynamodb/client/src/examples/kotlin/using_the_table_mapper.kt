import org.http4k.connect.amazon.dynamodb.DynamoDb
import org.http4k.connect.amazon.dynamodb.Http
import java.util.UUID

import org.http4k.connect.amazon.dynamodb.mapper.DynamoDbTableMapperSchema
import org.http4k.connect.amazon.dynamodb.mapper.tableMapper
import org.http4k.connect.amazon.dynamodb.model.Attribute
import org.http4k.connect.amazon.dynamodb.model.IndexName
import org.http4k.connect.amazon.dynamodb.model.TableName

// define our data class
private data class KittyCat(
    val ownerId: UUID,
    val name: String,
    val id: UUID = UUID.randomUUID()
)

// define our key attributes (for primary and secondary indexes)
private val idAttr = Attribute.uuid().required("id")
private val ownerIdAttr = Attribute.uuid().required("ownerId")

// define the primary index
private val primaryIndex = DynamoDbTableMapperSchema.Primary(idAttr)

// define an optional secondary index
private val ownerIndex = DynamoDbTableMapperSchema.GlobalSecondary(
    indexName = IndexName.of("owners"),
    hashKeyAttribute = ownerIdAttr,
    sortKeyAttribute = idAttr
)

fun main() {
    val dynamoDb = DynamoDb.Http(System.getenv())

    // define the table mapper and its primary index
    val table = dynamoDb.tableMapper<KittyCat, UUID, Unit>(
        TableName = TableName.of("cats"),
        primarySchema = primaryIndex
    )

    // optionally, create the table and its secondary indexes
    table.createTable(ownerIndex)

    // generate some documents
    val owner1 = UUID.randomUUID()
    val owner2 = UUID.randomUUID()

    val tigger = KittyCat(owner1, "Tigger")
    val smokie = KittyCat(owner2, "Smokie")
    val bandit = KittyCat(owner2, "Bandit")

    // add the documents to the table
    table += tigger  // ...individually
    table += listOf(smokie, bandit) // ...batched

    // get documents
    val cat = table[tigger.id] // individually
    val cats = table[tigger.id, smokie.id]  // batched

    // query documents
    val ownerCats = table.index(ownerIndex).query(owner2).take(100)

    // delete documents
    table -= tigger // ...individually
    table.delete(smokie.id)  // ...by key
    table.batchDelete(smokie.id, bandit.id) // ...batched
}
