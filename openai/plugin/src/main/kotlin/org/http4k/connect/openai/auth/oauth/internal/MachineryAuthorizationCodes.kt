package org.http4k.connect.openai.auth.oauth.internal

import dev.forkhandles.result4k.peek
import org.http4k.connect.openai.auth.oauth.OAuthMachinery
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.lens.Lens
import org.http4k.security.oauth.server.AuthRequest
import org.http4k.security.oauth.server.AuthorizationCode
import org.http4k.security.oauth.server.AuthorizationCodes

internal fun <Principal : Any> MachineryAuthorizationCodes(
    machinery: OAuthMachinery<Principal>,
    codePrincipalKey: Lens<Request, Principal>
) =
    object : AuthorizationCodes {
        override fun create(request: Request, authRequest: AuthRequest, response: Response) =
            machinery.create(request, authRequest, response)
                .peek { machinery[it] = codePrincipalKey(request) }

        override fun detailsFor(code: AuthorizationCode) = machinery.detailsFor(code)
    }
