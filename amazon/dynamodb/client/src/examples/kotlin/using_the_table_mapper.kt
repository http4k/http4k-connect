import org.http4k.connect.amazon.dynamodb.DynamoDb
import org.http4k.connect.amazon.dynamodb.Http
import java.util.UUID

import org.http4k.connect.amazon.dynamodb.mapper.DynamoDbTableMapperSchema
import org.http4k.connect.amazon.dynamodb.model.Attribute
import org.http4k.connect.amazon.dynamodb.model.IndexName
import org.http4k.connect.amazon.dynamodb.model.TableName
import org.http4k.connect.amazon.dynamodb.moshiTableMapper

// define our data class
private data class KittyCat(
    val ownerId: UUID,
    val name: String,
    val id: UUID = UUID.randomUUID()
)

// define our key attributes (for primary and secondary indexes)
private val idAttr = Attribute.uuid().required("id")  // primary hash key
private val ownerIdAttr = Attribute.uuid().required("ownerId")  // secondary hash key
private val nameAttr = Attribute.string().required("name")  // secondary sort key

// define an optional secondary index
private val byOwnerIndex = DynamoDbTableMapperSchema.GlobalSecondary(
    indexName = IndexName.of("owners"),
    hashKeyAttribute = ownerIdAttr,
    sortKeyAttribute = nameAttr
)

fun main() {
    val dynamoDb = DynamoDb.Http(System.getenv())

    // define the table mapper and its primary index
    val table = dynamoDb.moshiTableMapper<KittyCat, UUID, Unit>(
        TableName = TableName.of("cats"),
        hashKeyAttribute = idAttr
    )

    // optionally, create the table and its secondary indexes
    // table.createTable(byOwnerIndex)

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
    val bestCat = table[tigger.id]
    val missingCat = table[UUID.randomUUID()] // null

    // query documents
    val bestCats = table.primaryIndex().query(tigger.id).take(100) // by primary index
    val owner2Cats = table.index(byOwnerIndex).query(owner2).take(100) // by secondary index

    // delete documents
    table -= tigger // ...individually
    table.delete(smokie.id)  // ...by key
    table -= listOf(bandit) // ...batched
}
