package org.http4k.connect.openai.auth.oauth.impl

import org.http4k.connect.openai.auth.oauth.SecureStrings
import org.http4k.connect.storage.Storage
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.cookie.Cookie
import org.http4k.core.cookie.cookie
import org.http4k.security.oauth.server.AuthRequest
import org.http4k.security.oauth.server.AuthRequestTracking
import java.time.Clock
import java.time.Duration

fun StorageAuthRequestTracking(
    storage: Storage<AuthRequest>,
    cookieDomain: String,
    clock: Clock,
    strings: SecureStrings,
    validity: Duration
) = object : AuthRequestTracking {
    private val cookieName = "t"

    override fun resolveAuthRequest(request: Request): AuthRequest? =
        request.cookie(cookieName)
            ?.value
            ?.let { trackingId ->
                storage[trackingId]
                    ?.also { storage.remove(trackingId) }
            }

    override fun trackAuthRequest(request: Request, authRequest: AuthRequest, response: Response) =
        strings().let {
            storage[it] = authRequest
            response.cookie(expiring(cookieName, it, validity))
        }

    protected fun expiring(name: String, value: String, duration: Duration) = Cookie(
        name,
        value,
        expires = clock.instant().plus(duration),
        domain = cookieDomain,
        path = "/"
    )
}
