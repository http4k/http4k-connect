package org.http4k.connect.amazon

import org.http4k.aws.AwsCredentials
import org.http4k.cloudnative.env.Environment
import org.http4k.connect.amazon.core.credentials.CredentialsChain
import org.http4k.connect.amazon.core.credentials.Ec2InstanceProfile
import org.http4k.connect.amazon.core.credentials.Environment
import org.http4k.connect.amazon.core.credentials.Profile

fun interface CredentialsProvider : () -> AwsCredentials {
    companion object
}

fun CredentialsProvider.Companion.Environment(env: Environment) = CredentialsChain.Environment(env).provider()

fun CredentialsProvider.Companion.Environment(env: Map<String, String> = System.getenv()) =
    Environment(Environment.from(env))

fun CredentialsProvider.Companion.StandardChain(env: Map<String, String>) =
    StandardChain(Environment.from(env))

fun CredentialsProvider.Companion.StandardChain(env: Environment) = CredentialsChain.Environment(env)
    .orElse(CredentialsChain.Profile(env))
    .orElse(CredentialsChain.Ec2InstanceProfile())
    .provider()

