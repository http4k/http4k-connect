package org.http4k.connect.amazon.cognito.oauth

import org.http4k.connect.amazon.cognito.CognitoPool
import org.http4k.connect.storage.Storage
import org.http4k.core.Request
import org.http4k.core.Uri
import org.http4k.security.oauth.server.ClientId
import org.http4k.security.oauth.server.ClientValidator

class CognitoPoolClientValidator(private val storage: Storage<CognitoPool>) : ClientValidator {
    override fun validateClientId(request: Request, clientId: ClientId) =
        storage[clientId.value] != null

    override fun validateCredentials(request: Request, clientId: ClientId, clientSecret: String) =
        storage[clientId.value] != null

    override fun validateRedirection(request: Request, clientId: ClientId, redirectionUri: Uri)= true

    override fun validateScopes(request: Request, clientId: ClientId, scopes: List<String>) = true
}
