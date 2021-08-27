package org.http4k.connect.amazon

import org.http4k.cloudnative.env.Environment

fun CredentialsProvider.Companion.WebIdentity(env: Environment) = CredentialsProvider { AWS_CREDENTIALS(env) }
