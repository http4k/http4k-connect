package org.http4k.connect.amazon.ec2credentials.action

import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Success
import org.http4k.connect.Http4kConnectAction
import org.http4k.connect.RemoteFailure
import org.http4k.connect.amazon.ec2credentials.Ec2InstanceMetadata
import org.http4k.connect.amazon.ec2credentials.model.ImageId
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.Uri

@Http4kConnectAction
class GetAmiId: Ec2CredentialsAction<ImageId> {
    private val uri = Uri.of("/latest/meta-data/ami-id")

    override fun toRequest() = Request(Method.GET, uri)

    override fun toResult(response: Response) = when(response.status) {
        Status.OK -> response.bodyString()
            .lines()
            .first()
            .let(ImageId::of)
            .let(::Success)
        else -> Failure(RemoteFailure(Method.GET, uri, response.status, response.bodyString()))
    }
}

fun Ec2InstanceMetadata.getAmiId() = this(GetAmiId())
