package org.http4k.connect.amazon.cognito.oauth

import org.http4k.connect.amazon.cognito.CognitoPool
import org.http4k.connect.amazon.cognito.model.ClientSecret
import org.http4k.connect.storage.Storage
import org.http4k.core.Request
import org.http4k.core.Uri
import org.http4k.security.oauth.server.ClientId
import org.http4k.security.oauth.server.ClientValidator

class CognitoPoolClientValidator(private val storage: Storage<CognitoPool>) : ClientValidator {
    override fun validateClientId(request: Request, clientId: ClientId) =
        storage.hasAppClient(clientId)

    override fun validateCredentials(request: Request, clientId: ClientId, clientSecret: String) =
        storage.hasAppClient(clientId, ClientSecret.of(clientSecret))

    override fun validateRedirection(request: Request, clientId: ClientId, redirectionUri: Uri) = true

    override fun validateScopes(request: Request, clientId: ClientId, scopes: List<String>) = true
}

private fun Storage<CognitoPool>.hasAppClient(clientId: ClientId, secret: ClientSecret? = null) =
    keySet().any {
        this[it]!!.clients.any {
            it.ClientId.value == clientId.value && (secret == null || it.ClientSecret == secret)
        }
    }
