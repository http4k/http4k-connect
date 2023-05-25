package myplugin.service

import myplugin.service.ServicePluginSettings.PORT
import org.http4k.cloudnative.env.Environment.Companion.ENV
import org.http4k.core.then
import org.http4k.filter.CorsPolicy.Companion.UnsafeGlobalPermissive
import org.http4k.filter.ServerFilters.Cors
import org.http4k.filter.debug
import org.http4k.server.SunHttp
import org.http4k.server.asServer

/**
 * For running locally, we turn off CORs restrictions
 */
fun main() {
    Cors(UnsafeGlobalPermissive)
        .then(ServicePlugin(ENV))
        .debug()
        .asServer(SunHttp(PORT(ENV)))
        .start()
}
