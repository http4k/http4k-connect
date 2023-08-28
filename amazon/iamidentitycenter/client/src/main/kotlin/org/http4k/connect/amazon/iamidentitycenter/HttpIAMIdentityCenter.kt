package org.http4k.connect.amazon.iamidentitycenter

import org.http4k.client.JavaHttpClient
import org.http4k.cloudnative.env.Environment
import org.http4k.connect.amazon.AWS_REGION
import org.http4k.connect.amazon.core.model.Region
import org.http4k.core.HttpHandler
import org.http4k.core.then

fun IAMIdentityCenter.Companion.Http(
    region: Region,
    rawHttp: HttpHandler = JavaHttpClient(),
) = object : IAMIdentityCenter {
    private val http = setHostForAwsService(region, "").then(rawHttp)

    override fun <R : Any> invoke(action: IAMIdentityCenterAction<R>) = action.toResult(http(action.toRequest()))
}

/**
 * Convenience function to create a IAMIdentityCenter from a System environment
 */
fun IAMIdentityCenter.Companion.Http(
    env: Map<String, String> = System.getenv(),
    http: HttpHandler = JavaHttpClient(),
) = Http(Environment.from(env), http)

/**
 * Convenience function to create a IAMIdentityCenter from an http4k Environment
 */
fun IAMIdentityCenter.Companion.Http(
    env: Environment,
    http: HttpHandler = JavaHttpClient(),
) = Http(AWS_REGION(env), http)
