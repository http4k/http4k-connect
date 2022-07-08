package org.http4k.connect.amazon

import org.http4k.aws.AwsCredentials
import org.http4k.cloudnative.env.Environment

fun interface CredentialsProvider : () -> AwsCredentials {
    companion object
}

fun CredentialsProvider.Companion.Environment(env: Environment) = CredentialsProvider { AWS_CREDENTIALS(env) }

fun CredentialsProvider.Companion.Environment(env: Map<String, String> = System.getenv()) =
    Environment(Environment.from(env))
