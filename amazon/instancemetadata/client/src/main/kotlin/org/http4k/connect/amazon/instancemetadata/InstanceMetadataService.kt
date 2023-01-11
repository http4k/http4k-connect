package org.http4k.connect.amazon.instancemetadata

import org.http4k.connect.Http4kConnectAdapter
import org.http4k.connect.RemoteFailure
import org.http4k.connect.amazon.instancemetadata.action.Ec2MetadataAction
import dev.forkhandles.result4k.Result

@Http4kConnectAdapter
interface InstanceMetadataService {
    operator fun <R> invoke(action: Ec2MetadataAction<R>): Result<R, RemoteFailure>

    companion object
}
