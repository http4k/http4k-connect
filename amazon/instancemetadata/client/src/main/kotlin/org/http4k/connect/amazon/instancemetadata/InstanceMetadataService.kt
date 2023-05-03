package org.http4k.connect.amazon.instancemetadata

import dev.forkhandles.result4k.Result
import org.http4k.connect.Http4kConnectAdapter
import org.http4k.connect.RemoteFailure

@Http4kConnectAdapter
interface InstanceMetadataService {
    operator fun <R> invoke(action: Ec2MetadataAction<R>): Result<R, RemoteFailure>

    companion object
}
