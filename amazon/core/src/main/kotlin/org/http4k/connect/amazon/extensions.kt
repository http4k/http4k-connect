package org.http4k.connect.amazon

import org.http4k.aws.AwsCredentials
import org.http4k.cloudnative.env.Environment
import org.http4k.cloudnative.env.EnvironmentKey
import org.http4k.connect.amazon.core.model.AccessKeyId
import org.http4k.connect.amazon.core.model.Region
import org.http4k.connect.amazon.core.model.SecretAccessKey
import org.http4k.connect.amazon.core.model.SessionToken
import org.http4k.lens.composite
import org.http4k.lens.value

fun Map<String, String>.awsCredentials(): () -> AwsCredentials =
    {
        AWS_CREDENTIALS(Environment.from(this))
    }

val AWS_REGION = EnvironmentKey.value(Region).required("AWS_REGION")
val AWS_ACCESS_KEY_ID = EnvironmentKey.value(AccessKeyId).required("AWS_ACCESS_KEY_ID")
val AWS_SECRET_ACCESS_KEY = EnvironmentKey.value(SecretAccessKey).required("AWS_SECRET_ACCESS_KEY")
val AWS_SESSION_TOKEN = EnvironmentKey.value(SessionToken).optional("AWS_SESSION_TOKEN")

val AWS_CREDENTIALS = EnvironmentKey.composite {
    AwsCredentials(
        AWS_ACCESS_KEY_ID(it).value,
        AWS_SECRET_ACCESS_KEY(it).value,
        AWS_SESSION_TOKEN(it)?.value
    )
}
