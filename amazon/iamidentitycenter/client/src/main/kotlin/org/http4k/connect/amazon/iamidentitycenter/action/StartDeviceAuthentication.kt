package org.http4k.connect.amazon.iamidentitycenter.action

import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Success
import org.http4k.connect.Http4kConnectAction
import org.http4k.connect.amazon.core.model.ClientId
import org.http4k.connect.amazon.core.model.ClientSecret
import org.http4k.connect.amazon.iamidentitycenter.IAMIdentityCenterAction
import org.http4k.connect.amazon.iamidentitycenter.IAMIdentityCenterMoshi
import org.http4k.connect.amazon.iamidentitycenter.IAMIdentityCenterMoshi.asFormatString
import org.http4k.connect.amazon.model.Code
import org.http4k.connect.amazon.model.DeviceCode
import org.http4k.connect.amazon.model.ExpiresIn
import org.http4k.connect.amazon.model.Interval
import org.http4k.connect.asRemoteFailure
import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Uri
import se.ansman.kotshi.JsonSerializable

@Http4kConnectAction
@JsonSerializable
data class StartDeviceAuthentication(
    val clientId: ClientId,
    val clientSecret: ClientSecret,
    val startUrl: Uri,
) : IAMIdentityCenterAction<AuthCodeResponse> {
    override fun toRequest() = Request(POST, "/device_authorization").body(asFormatString(this))

    override fun toResult(response: Response) = with(response) {
        when {
            status.successful -> Success(IAMIdentityCenterMoshi.asA<AuthCodeResponse>(bodyString()))
            else -> Failure(asRemoteFailure(this))
        }
    }
}

@JsonSerializable
data class AuthCodeResponse(
    val deviceCode: DeviceCode,
    val expiresIn: ExpiresIn,
    val interval: Interval,
    val userCode: Code,
    val verificationUri: Uri,
    val verificationUriComplete: Uri
)
