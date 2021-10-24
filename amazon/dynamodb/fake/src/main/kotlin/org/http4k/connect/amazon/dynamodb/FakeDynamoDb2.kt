package org.http4k.connect.amazon.dynamodb

import com.amazonaws.services.dynamodbv2.local.server.LocalDynamoDBRequestHandler
import com.amazonaws.services.dynamodbv2.local.server.LocalDynamoDBServerHandler
import org.http4k.aws.AwsCredentials
import org.http4k.chaos.ChaoticHttpHandler
import org.http4k.connect.amazon.core.model.Region
import org.http4k.connect.amazon.dynamodb.model.Key
import org.http4k.connect.amazon.dynamodb.model.TableName
import org.http4k.core.Request
import java.lang.Integer.MAX_VALUE
import java.time.Clock
import java.time.Clock.systemUTC

class FakeDynamoDb2(
    private val clock: Clock = systemUTC()
) : ChaoticHttpHandler() {

    private val dynamo = LocalDynamoDBServerHandler(
        LocalDynamoDBRequestHandler(
            MAX_VALUE,
            true,
            null,
            true,
            false
        ), null
    )
    override val app = { req: Request ->
        FakeServletResponse().also {
            val request = FakeServletRequest(req)
            dynamo.handle("", org.eclipse.jetty.server.Request(null, null), request, it)
        }.build()
    }

    /**
     * Convenience function to get DynamoDb client
     */
    fun client() = DynamoDb.Http(Region.of("us-east-1"), { AwsCredentials("accessKey", "secret") }, this, clock)
}

fun main() {
    println(FakeDynamoDb2().client().getItem(TableName.of("asd"), Key()))
}
