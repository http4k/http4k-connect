package addressbook.oauth.auth

import addressbook.shared.UserDirectory
import addressbook.shared.UserId
import org.http4k.connect.openai.auth.oauth.Authenticate
import org.http4k.core.Credentials
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK
import org.http4k.core.body.form

/**
 * This is responsible for presenting the login challenge to the user and resolving
 * the details of that challenge when posted back.
 */
fun LoginAuthenticate(userDirectory: UserDirectory) = object : Authenticate<UserId> {
    override val challenge = { _: Request ->
        Response(OK).body(
            """
            <html>
                <form method="POST">
                    <input name="userId"/>
                    <input name="password"/>
                    <button type="submit">Please authenticate</button>
                </form>
            </html>
            """.trimIndent()
        )
    }

    override fun invoke(request: Request) =
        userDirectory.auth(Credentials(request.form("userId")!!, request.form("password")!!))
            ?.credentials?.user?.let(UserId::of)
}
