package org.http4k.connect.amazon.ec2credentials.action

import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Success
import org.http4k.connect.Http4kConnectAction
import org.http4k.connect.RemoteFailure
import org.http4k.connect.amazon.core.model.Ec2Credentials
import org.http4k.connect.amazon.core.model.Ec2ProfileName
import org.http4k.connect.amazon.ec2credentials.Ec2InstanceMetadata
import org.http4k.connect.amazon.ec2credentials.Ec2InstanceMetadataMoshi
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Uri

@Http4kConnectAction
data class GetEc2Credentials(private val ec2ProfileName: Ec2ProfileName): Ec2CredentialsAction<Ec2Credentials> {

    private val uri = Uri.of("/latest/meta-data/iam/security-credentials/$ec2ProfileName")
    private val lens = Ec2InstanceMetadataMoshi.autoBody<Ec2Credentials>().toLens()

    override fun toRequest() = Request(Method.GET, uri)

    override fun toResult(response: Response) = with(response) {
        if (status.successful) {
            Success(lens(response))
        } else {
            Failure(RemoteFailure(Method.GET, uri, response.status, response.bodyString()))
        }
    }
}

fun Ec2InstanceMetadata.getCredentials(ec2ProfileName: Ec2ProfileName) = this(GetEc2Credentials(ec2ProfileName))
