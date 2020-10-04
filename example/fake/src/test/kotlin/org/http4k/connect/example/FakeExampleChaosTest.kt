package org.http4k.connect.example

import org.http4k.connect.FakeSystemContract
import org.http4k.core.Method
import org.http4k.core.Request

class FakeExampleChaosTest : FakeSystemContract(FakeExample()) {
    override val anyValidRequest = Request(Method.POST, "/")
}
