package org.http4k.connect.amazon.s3

import org.http4k.aws.AwsCredentialScope
import org.http4k.aws.AwsCredentials
import org.http4k.client.JavaHttpClient
import org.http4k.cloudnative.env.Environment
import org.http4k.cloudnative.env.EnvironmentKey
import org.http4k.cloudnative.env.fromConfigFile
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Uri
import org.http4k.core.then
import org.http4k.filter.AwsAuth
import org.http4k.filter.ClientFilters
import org.http4k.filter.DebuggingFilters
import org.http4k.lens.composite
import java.io.File

fun main() {
    val env = Environment.fromConfigFile(File(System.getProperty("user.home"), ".aws/config")) overrides
        Environment.fromConfigFile(File(System.getProperty("user.home"), ".aws/credentials"))

    val credentials = EnvironmentKey.composite {
        AwsCredentials(
            EnvironmentKey.required("default-aws-access-key-id")(it),
            EnvironmentKey.required("default-aws-secret-access-key")(it)
        )
    }(env)
    val region = EnvironmentKey.required("default-region")(env)
    val scope = AwsCredentialScope(region, "s3")

    val http = ClientFilters.AwsAuth(scope, credentials).then(DebuggingFilters.PrintRequestAndResponse()).then(JavaHttpClient())

    val message = http(Request(Method.GET, Uri.of("https://s3.$region.amazonaws.com/"))).bodyString()

    val d = documentBuilderFactory.parse(message.byteInputStream())

    println(d.documentElement.getElementsByTagName("Name").length)
}


