package org.http4k.connect.amazon

import org.http4k.aws.AwsCredentials
import org.http4k.cloudnative.env.Environment

fun interface CredentialsProvider : () -> AwsCredentials {
    companion object
}

fun CredentialsProvider.Companion.Environment(env: Environment) = object : CredentialsProvider {
    override fun invoke(): AwsCredentials = AWS_CREDENTIALS(env)
}
