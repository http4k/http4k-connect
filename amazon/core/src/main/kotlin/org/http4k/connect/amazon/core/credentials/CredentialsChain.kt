package org.http4k.connect.amazon.core.credentials

import org.http4k.aws.AwsCredentials
import org.http4k.cloudnative.env.Environment
import org.http4k.connect.amazon.AWS_ACCESS_KEY_ID
import org.http4k.connect.amazon.AWS_SECRET_ACCESS_KEY
import org.http4k.connect.amazon.AWS_SESSION_TOKEN
import org.http4k.connect.amazon.CredentialsProvider

fun interface CredentialsChain: () -> AwsCredentials? {
    infix fun orElse(next: CredentialsChain) = CredentialsChain { this() ?: next() }
    fun provider() = CredentialsProvider { this() ?: throw IllegalArgumentException("Could not find any valid credentials in the chain") }
    companion object
}

fun CredentialsChain.Companion.Environment(env: Environment) = CredentialsChain {
    val accessKey = AWS_ACCESS_KEY_ID(env)
    val secretKey = AWS_SECRET_ACCESS_KEY(env)
    if (accessKey == null || secretKey == null) null else {
        AwsCredentials(
            accessKey = accessKey.value,
            secretKey = secretKey.value,
            sessionToken = AWS_SESSION_TOKEN(env)?.value
        )
    }
}
