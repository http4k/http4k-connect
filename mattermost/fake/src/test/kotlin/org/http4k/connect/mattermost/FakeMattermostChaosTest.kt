package org.http4k.connect.mattermost

import org.http4k.connect.FakeSystemContract
import org.http4k.core.Method
import org.http4k.core.Request

class FakeMattermostChaosTest : FakeSystemContract(FakeMattermost()) {
    override val anyValid = Request(Method.POST, "/")
}
