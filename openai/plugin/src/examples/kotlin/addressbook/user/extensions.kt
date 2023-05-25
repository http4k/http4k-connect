package addressbook.user

import addressbook.shared.UserDirectory
import addressbook.shared.UserId
import org.http4k.connect.openai.auth.AuthToken
import org.http4k.lens.RequestContextLens

/**
 * Populate a known user if their password matches
 */
fun UserDirectory.authUser(userPrincipal: RequestContextLens<UserId>) =
    AuthToken.Basic("realm", userPrincipal) { credentials ->
        UserId.of(credentials.user)
            .takeIf { auth(credentials) }
    }
