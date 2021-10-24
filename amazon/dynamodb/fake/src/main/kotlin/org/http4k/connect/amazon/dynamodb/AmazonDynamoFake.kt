package org.http4k.connect.amazon.dynamodb

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import com.amazonaws.services.dynamodbv2.local.embedded.DynamoDBEmbedded
import com.amazonaws.services.dynamodbv2.local.shared.mapper.DynamoDBObjectMapper
import org.http4k.connect.amazon.JsonError
import org.http4k.connect.amazon.core.model.AwsService
import org.http4k.core.Response
import org.http4k.core.Status.Companion.BAD_REQUEST
import org.http4k.core.Status.Companion.OK
import org.http4k.routing.bind
import org.http4k.routing.header

class AmazonDynamoFake {
    val embedded: AmazonDynamoDB = DynamoDBEmbedded.create().amazonDynamoDB()

    val awsService = AwsService.of("DynamoDB_20120810")
    val mapper = DynamoDBObjectMapper()
    inline fun <reified Req : Any> route(crossinline fn: AmazonDynamoDB.(Req) -> Any?) =
        header("X-Amz-Target", "${awsService}.${Req::class.simpleName!!.removeSuffix("Request")}") bind {
            embedded.fn(mapper.readValue(it.bodyString(), Req::class.java))
                ?.let { Response(OK).body(mapper.writeValueAsString(it)) }
                ?: Response(BAD_REQUEST)
                    .body(
                        mapper.writeValueAsString(
                            JsonError(
                                "ResourceNotFoundException",
                                "$awsService can't find the specified item."
                            )
                        )
                    )
        }
}
