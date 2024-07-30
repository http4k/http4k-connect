package org.http4k.connect.amazon

import org.http4k.core.HttpHandler

interface AwsContract {
    val aws: AwsEnvironment
    val http: HttpHandler
}
