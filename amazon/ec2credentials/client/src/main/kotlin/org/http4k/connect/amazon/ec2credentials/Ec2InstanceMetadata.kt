package org.http4k.connect.amazon.ec2credentials

import org.http4k.connect.Http4kConnectAdapter
import org.http4k.connect.RemoteFailure
import org.http4k.connect.amazon.ec2credentials.action.Ec2CredentialsAction
import dev.forkhandles.result4k.Result
import org.http4k.connect.amazon.AwsServiceCompanion

@Deprecated("Use http4k-connect-amazon-instancemetadata module")
@Http4kConnectAdapter
interface Ec2InstanceMetadata {
    operator fun <R> invoke(action: Ec2CredentialsAction<R>): Result<R, RemoteFailure>

    companion object : AwsServiceCompanion("ec2credentials")
}
