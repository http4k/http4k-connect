package org.http4k.connect.amazon.cognito

import org.http4k.client.JavaHttpClient
import org.http4k.connect.amazon.RealAwsEnvironment
import org.http4k.connect.amazon.configAwsEnvironment

class RealCognitoTest : CognitoContract(JavaHttpClient()), RealAwsEnvironment {
    override val aws get() = configAwsEnvironment()
}
