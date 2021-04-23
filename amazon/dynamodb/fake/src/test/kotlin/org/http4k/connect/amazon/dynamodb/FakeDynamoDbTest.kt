package org.http4k.connect.amazon.dynamodb

import org.http4k.connect.amazon.fakeAwsEnvironment
import org.http4k.filter.debug
import java.time.Duration.ZERO

class FakeDynamoDbTest : DynamoDbContract(ZERO) {
    override val http = FakeDynamoDb().debug()

    override val aws = fakeAwsEnvironment
}
