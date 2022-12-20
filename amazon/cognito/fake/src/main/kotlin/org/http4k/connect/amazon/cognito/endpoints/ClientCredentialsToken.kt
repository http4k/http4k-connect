package org.http4k.connect.amazon.cognito.endpoints

import org.http4k.connect.amazon.cognito.CognitoPool
import org.http4k.connect.storage.Storage
import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK
import org.http4k.core.then
import org.http4k.filter.ServerFilters
import org.http4k.routing.bind
import org.http4k.security.AccessTokenResponse
import org.http4k.security.oauth.server.OAuthServerMoshi
import java.time.Clock
import java.time.Duration
import java.util.UUID

fun clientCredentialsToken(clock: Clock, expiry: Duration, storage: Storage<CognitoPool>) =
    "/oauth2/token/" bind POST to ServerFilters.BasicAuth("") { storage[it.user] != null }
        .then { req: Request ->
            val key = (req.header("Authorization") ?: "") + clock.instant().toEpochMilli()
            Response(OK).body(
                OAuthServerMoshi.asFormatString(
                    AccessTokenResponse(
                        UUID.nameUUIDFromBytes(key.toByteArray()).toString(),
                        "Bearer",
                        expiry.seconds
                    )
                )
            )
        }
