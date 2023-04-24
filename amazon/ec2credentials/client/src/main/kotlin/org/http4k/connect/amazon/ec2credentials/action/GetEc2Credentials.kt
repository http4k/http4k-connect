package org.http4k.connect.amazon.ec2credentials.action

import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Result
import dev.forkhandles.result4k.Success
import org.http4k.connect.Http4kConnectAction
import org.http4k.connect.RemoteFailure
import org.http4k.connect.amazon.core.model.Ec2ProfileName
import org.http4k.connect.amazon.ec2credentials.Ec2Credentials
import org.http4k.connect.amazon.ec2credentials.Ec2InstanceMetadata
import org.http4k.connect.amazon.ec2credentials.Ec2InstanceMetadataMoshi
import org.http4k.connect.toRemoteFailure
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Uri

@Http4kConnectAction
data class GetEc2Credentials(private val ec2ProfileName: Ec2ProfileName): Ec2CredentialsAction<Ec2Credentials> {

    private fun uri() = Uri.of("/latest/meta-data/iam/security-credentials/$ec2ProfileName")

    override fun toRequest() = Request(Method.GET, uri())

    override fun toResult(response: Response): Result<Ec2Credentials, RemoteFailure> = with(response) {
        if (status.successful) {
            Success(Ec2InstanceMetadataMoshi.asA(response.bodyString()))
        } else {
            Failure(toRemoteFailure(this))
        }
    }
}

fun Ec2InstanceMetadata.getCredentials(ec2ProfileName: Ec2ProfileName) = this(GetEc2Credentials(ec2ProfileName))
