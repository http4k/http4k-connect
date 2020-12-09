package org.http4k.connect.amazon

import org.http4k.connect.amazon.model.AwsService

interface RealAwsEnvironment {
    val service: AwsService
    val aws: AwsEnvironment
}
