package org.http4k.connect.amazon.dynamodb

import org.http4k.connect.amazon.fakeAwsEnvironment
import org.junit.jupiter.api.Disabled
import java.time.Duration.ZERO

class FakeDynamoDbTest : DynamoDbContract(ZERO) {
    override val http = FakeDynamoDb()

    override val aws = fakeAwsEnvironment

    @Disabled
    override fun `partiSQL operations`() {
        TODO("support")
    }
}
