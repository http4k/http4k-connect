package org.http4k.connect.amazon.cognito

import org.http4k.connect.amazon.fakeAwsEnvironment

class FakeCognitoTest : CognitoContract(FakeCognito()) {
    override val aws = fakeAwsEnvironment
}
