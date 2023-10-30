package org.http4k.connect.amazon.iamidentitycenter

import dev.forkhandles.result4k.Result
import org.http4k.connect.Http4kConnectAdapter
import org.http4k.connect.RemoteFailure
import org.http4k.connect.amazon.AwsServiceCompanion

/**
 * Docs: https://docs.aws.amazon.com/singlesignon/latest/OIDCAPIReference/Welcome.html
 */
@Http4kConnectAdapter
interface OIDC {
    operator fun <R : Any> invoke(action: OIDCAction<R>): Result<R, RemoteFailure>

    companion object : AwsServiceCompanion("oidc")
}

