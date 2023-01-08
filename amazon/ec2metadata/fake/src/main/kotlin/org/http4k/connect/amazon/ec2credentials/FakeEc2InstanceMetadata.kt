package org.http4k.connect.amazon.ec2credentials

import org.http4k.chaos.ChaoticHttpHandler
import org.http4k.chaos.start
import org.http4k.connect.amazon.ec2credentials.endpoints.getAmiId
import org.http4k.connect.amazon.ec2credentials.endpoints.getCredentials
import org.http4k.connect.amazon.ec2credentials.endpoints.getHostName
import org.http4k.connect.amazon.ec2credentials.endpoints.getIdentityDocument
import org.http4k.connect.amazon.ec2credentials.endpoints.getInstanceId
import org.http4k.connect.amazon.ec2credentials.endpoints.getInstanceType
import org.http4k.connect.amazon.ec2credentials.endpoints.getPublicHostName
import org.http4k.connect.amazon.ec2credentials.endpoints.getLocalHostName
import org.http4k.connect.amazon.ec2credentials.endpoints.getLocalIpV4
import org.http4k.connect.amazon.ec2credentials.endpoints.getPublicIpV4
import org.http4k.connect.amazon.ec2credentials.endpoints.listCredentials
import org.http4k.routing.routes
import java.time.Clock

class FakeEc2InstanceMetadata(
    clock: Clock = Clock.systemUTC(),
    metadata: InstanceMetadata = InstanceMetadata(clock.instant())
) : ChaoticHttpHandler() {

    override val app = routes(
        listCredentials(metadata),
        getCredentials(metadata, clock),
        getPublicHostName(metadata),
        getLocalHostName(metadata),
        getHostName(metadata),
        getPublicIpV4(metadata),
        getLocalIpV4(metadata),
        getIdentityDocument(metadata),
        getAmiId(metadata),
        getInstanceId(metadata),
        getInstanceType(metadata)
    )

    fun client() = Ec2InstanceMetadata.Http(this)
}

fun main() {
    FakeEc2InstanceMetadata().start()
}
