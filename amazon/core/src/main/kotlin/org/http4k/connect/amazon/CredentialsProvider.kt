package org.http4k.connect.amazon

import org.http4k.aws.AwsCredentials
import org.http4k.cloudnative.env.Environment
import org.http4k.cloudnative.env.Environment.Companion.ENV

fun interface CredentialsProvider : () -> AwsCredentials {
    companion object
}

fun CredentialsProvider.Companion.Environment(env: Environment = ENV) = CredentialsProvider { AWS_CREDENTIALS(env) }
