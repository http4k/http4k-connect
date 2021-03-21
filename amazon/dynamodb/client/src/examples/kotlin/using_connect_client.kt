import dev.forkhandles.result4k.Result
import org.http4k.aws.AwsCredentials
import org.http4k.client.JavaHttpClient
import org.http4k.connect.RemoteFailure
import org.http4k.connect.amazon.dynamodb.DynamoDb
import org.http4k.connect.amazon.dynamodb.Http
import org.http4k.connect.amazon.dynamodb.action.TableDescriptionResponse
import org.http4k.connect.amazon.dynamodb.deleteTable
import org.http4k.connect.amazon.model.Region
import org.http4k.connect.amazon.model.TableName
import org.http4k.core.HttpHandler
import org.http4k.filter.debug

fun main() {
    // we can connect to the real service
    val http: HttpHandler = JavaHttpClient()

    // create a client
    val client = DynamoDb.Http(Region.of("us-east-1"), { AwsCredentials("accessKeyId", "secretKey") }, http.debug())

    // all operations return a Result monad of the API type
    val deleteResult: Result<TableDescriptionResponse, RemoteFailure> = client.deleteTable(TableName.of("myTable"))
    println(deleteResult)
}

