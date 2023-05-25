package addressbook.oauth

import addressbook.shared.UserDirectory
import addressbook.shared.UserId
import org.http4k.connect.openai.auth.AuthChallenge
import org.http4k.core.Credentials
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.body.form

fun UserIdAuthChallenge(
    userDirectory: UserDirectory
) = object : AuthChallenge<UserId> {
    override val challenge = { _: Request -> Response(Status.OK) }

    override fun invoke(request: Request) =
        userDirectory.auth(Credentials(request.form("userId")!!, request.form("password")!!))
            ?.credentials?.user?.let(UserId::of)
}
