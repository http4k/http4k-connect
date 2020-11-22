package org.http4k.connect.amazon

import org.http4k.aws.AwsCredentialScope
import org.http4k.aws.AwsCredentials
import org.http4k.cloudnative.env.Environment
import org.http4k.cloudnative.env.EnvironmentKey
import org.http4k.cloudnative.env.fromConfigFile
import org.http4k.lens.composite
import org.junit.jupiter.api.Assumptions.assumeTrue
import java.io.File

data class AwsEnvironment(val credentials: AwsCredentials, val scope: AwsCredentialScope)

val fakeAwsEnvironment = AwsEnvironment(AwsCredentials("key", "keyid"),
    AwsCredentialScope("ldn-north-1", "svc")
)

fun configAwsEnvironment(service: String): AwsEnvironment {
    val config = File(System.getProperty("user.home"), ".aws/config").apply { assumeTrue(exists()) }
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
        AwsCredentialScope(region, service)
    )
}
