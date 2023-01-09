package org.http4k.connect.amazon.instancemetadata

import org.http4k.connect.Http4kConnectAdapter
import org.http4k.connect.RemoteFailure
import org.http4k.connect.amazon.instancemetadata.action.Ec2CredentialsAction
import dev.forkhandles.result4k.Result
import org.http4k.connect.amazon.AwsServiceCompanion

@Deprecated("Use http4k-connect-amazon-ec2metadata module")
@Http4kConnectAdapter
interface Ec2InstanceMetadata {
    operator fun <R> invoke(action: Ec2CredentialsAction<R>): Result<R, RemoteFailure>

    companion object : AwsServiceCompanion("ec2credentials")
}
