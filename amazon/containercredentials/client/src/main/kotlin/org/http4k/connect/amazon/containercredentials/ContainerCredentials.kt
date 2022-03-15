package org.http4k.connect.amazon.containercredentials

import dev.forkhandles.result4k.Result
import org.http4k.connect.Http4kConnectAdapter
import org.http4k.connect.RemoteFailure
import org.http4k.connect.amazon.AwsServiceCompanion
import org.http4k.connect.amazon.containercredentials.action.ContainerCredentialsAction

@Http4kConnectAdapter
interface ContainerCredentials {
    operator fun <R> invoke(action: ContainerCredentialsAction<R>): Result<R, RemoteFailure>

    companion object : AwsServiceCompanion("containercredentials")
}
