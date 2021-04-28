package org.http4k.connect.amazon.cognito

import dev.forkhandles.result4k.Result
import org.http4k.connect.Http4kConnectAdapter
import org.http4k.connect.RemoteFailure
import org.http4k.connect.amazon.AwsServiceCompanion
import org.http4k.connect.amazon.cognito.action.CognitoAction

/**
 * Docs: https://docs.aws.amazon.com/cognito-user-identity-pools/latest/APIReference/Welcome.html
 */
@Http4kConnectAdapter
interface Cognito {
    operator fun <R : Any> invoke(action: CognitoAction<R>): Result<R, RemoteFailure>

    companion object : AwsServiceCompanion("cognito-idp")
}
