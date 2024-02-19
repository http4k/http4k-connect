package org.http4k.connect.amazon.iamidentitycenter

import org.http4k.connect.amazon.fakeAwsEnvironment
import org.http4k.routing.reverseProxy

class FakeIAMIdentityCenterTest : IAMIdentityCenterContract(
    reverseProxy(
        "sso" to FakeSSO(),
        "oidc" to FakeOIDC()
    )
) {
    override val aws = fakeAwsEnvironment
}
