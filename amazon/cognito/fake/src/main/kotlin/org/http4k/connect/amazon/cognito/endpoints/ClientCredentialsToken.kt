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

/**
 * We only check here for the existence of the client ID in order to issue a token
 */
fun clientCredentialsToken(clock: Clock, expiry: Duration, storage: Storage<CognitoPool>) =
    "/oauth2/token/" bind POST to ServerFilters.BasicAuth("") { creds ->
        storage.keySet().any { storage[it]!!.clients.any { it.ClientId.value == creds.user } }
    }
        .then { req: Request ->
            val key = (req.header("Authorization") ?: "") + clock.instant().toEpochMilli()
            Response(OK).body(
                OAuthServerMoshi.asFormatString(
                    AccessTokenResponse(
                        // TODO - replace with a JWT
                        UUID.nameUUIDFromBytes(key.toByteArray()).toString(),
                        "Bearer",
                        expiry.seconds
                    )
                )
            )
        }
