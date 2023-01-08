package org.http4k.connect.amazon.ec2credentials.model

import org.http4k.connect.amazon.core.model.Region
import java.time.Instant

data class IdentityDocument(
    val pendingTime: Instant,
    val accountId: String,
    val architecture: String,
    val imageId: ImageId,
    val instanceId: InstanceId,
    val instanceType: InstanceType,
    val privateIp: IpV4Address,
    val region: Region,
    val availabilityZone: String,
    val version: String
)
