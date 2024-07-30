package org.http4k.connect.amazon.dynamodb

import org.http4k.connect.amazon.FakeAwsContract
import org.junit.jupiter.api.Disabled
import java.time.Duration.ZERO

class FakeDynamoDbTest : DynamoDbContract(ZERO), FakeAwsContract {
    override val http = FakeDynamoDb()

    @Disabled
    override fun `partiSQL operations`() {
    }
}
