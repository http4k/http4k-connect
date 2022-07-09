package org.http4k.connect.amazon.ec2credentials

import org.http4k.base64Encode
import org.http4k.chaos.ChaoticHttpHandler
import org.http4k.chaos.start
import org.http4k.connect.amazon.core.model.AccessKeyId
import org.http4k.connect.amazon.core.model.Ec2Credentials
import org.http4k.connect.amazon.core.model.Ec2ProfileName
import org.http4k.connect.amazon.core.model.Expiration
import org.http4k.connect.amazon.core.model.SecretAccessKey
import org.http4k.connect.amazon.core.model.SessionToken
import org.http4k.core.Method.GET
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.Status.Companion.OK
import org.http4k.lens.Path
import org.http4k.lens.value
import org.http4k.routing.bind
import org.http4k.routing.routes
import java.time.Clock
import java.time.Duration
import java.time.Duration.ofHours
import java.time.ZonedDateTime
import java.util.UUID

class FakeEc2CredentialService(
    private val clock: Clock = Clock.systemUTC(),
    private val defaultSessionValidity: Duration = ofHours(1),
    val profiles: MutableSet<Ec2ProfileName> = mutableSetOf(Ec2ProfileName.of("default"))
) : ChaoticHttpHandler() {

    private val profileNameLens = Path.value(Ec2ProfileName).of("profile_name")

    var generatedCredentials = emptyList<Ec2Credentials>()
        private set

    private fun listProfiles() = Response(OK).body(profiles.joinToString("\n"))

    private fun getCredentials(request: Request): Response {
        val profileName = profileNameLens(request)
        if (profileName !in profiles) {
            return Response(Status.NOT_FOUND)
        }

        val credentials = Ec2Credentials(
            Code = "Success",
            LastUpdated = ZonedDateTime.now(clock),
            Type = "AWS-HMAC",
            AccessKeyId = AccessKeyId.of(UUID.randomUUID().toString()),
            SecretAccessKey = SecretAccessKey.of(UUID.randomUUID().toString()),
            Token = SessionToken.of(UUID.randomUUID().toString().base64Encode()),
            Expiration = Expiration.of(ZonedDateTime.now(clock) + defaultSessionValidity),
        )
        generatedCredentials = generatedCredentials + credentials

        return Response(OK).body(Ec2CredentialsMoshi.asFormatString(credentials))
    }

    override val app = routes(
        "/latest/meta-data/iam/security-credentials" bind GET to { listProfiles() },
        "/latest/meta-data/iam/security-credentials/$profileNameLens" bind GET to ::getCredentials
    )

    fun client() = Ec2InstanceMetadata.Http(this)
}

fun main() {
    FakeEc2CredentialService().start()
}
