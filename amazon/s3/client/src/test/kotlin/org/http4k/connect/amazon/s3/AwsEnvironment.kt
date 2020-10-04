package org.http4k.connect.amazon.s3

import org.http4k.aws.AwsCredentialScope
import org.http4k.aws.AwsCredentials
import org.http4k.cloudnative.env.Environment
import org.http4k.cloudnative.env.EnvironmentKey
import org.http4k.cloudnative.env.fromConfigFile
import org.http4k.core.Uri
import org.http4k.lens.composite
import org.junit.jupiter.api.Assumptions
import java.io.File

data class AwsEnvironment(val credentials: AwsCredentials, val scope: AwsCredentialScope, val uri: Uri)

val fakeAwsEnvironment = AwsEnvironment(AwsCredentials("key", "keyid"),
    AwsCredentialScope("ldn-north-1", "s3"),
    Uri.of("http://fakeS3Bucket")
)

fun configAwsEnvironment(): AwsEnvironment {
    val config = File(System.getProperty("user.home"), ".aws/config").apply { Assumptions.assumeTrue(exists()) }
    val env = Environment.fromConfigFile(config) overrides
        Environment.fromConfigFile(File(System.getProperty("user.home"), ".aws/credentials"))

    val region = EnvironmentKey.required("default-region")(env)
    return AwsEnvironment(
        EnvironmentKey.composite {
            AwsCredentials(
                EnvironmentKey.required("default-aws-access-key-id")(it),
                EnvironmentKey.required("default-aws-secret-access-key")(it)
            )
        }(env),
        AwsCredentialScope(region, "s3"),
        Uri.of("https://s3.amazonaws.com/")
    )
}
