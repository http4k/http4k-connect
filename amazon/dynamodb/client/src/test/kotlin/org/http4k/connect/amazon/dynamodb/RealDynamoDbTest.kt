package org.http4k.connect.amazon.dynamodb

import org.http4k.client.JavaHttpClient
import org.http4k.connect.amazon.RealAwsEnvironment
import org.http4k.connect.amazon.configAwsEnvironment
import org.junit.jupiter.api.Disabled

@Disabled
class RealDynamoDbTest : DynamoDbContract(JavaHttpClient()), RealAwsEnvironment {
    override val aws get() = configAwsEnvironment()
}
