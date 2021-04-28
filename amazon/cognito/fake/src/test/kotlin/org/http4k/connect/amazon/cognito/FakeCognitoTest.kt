package org.http4k.connect.amazon.cognito

import org.http4k.connect.amazon.fakeAwsEnvironment
import org.junit.jupiter.api.Disabled

@Disabled
class FakeCognitoTest : CognitoContract(FakeCognito()) {
    override val aws = fakeAwsEnvironment
}
