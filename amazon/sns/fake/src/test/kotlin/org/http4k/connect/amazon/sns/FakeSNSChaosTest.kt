package org.http4k.connect.amazon.sns

import org.http4k.connect.FakeSystemContract
import org.http4k.connect.amazon.sqs.FakeSQS
import org.http4k.core.Method.GET
import org.http4k.core.Request

class FakeSNSChaosTest : FakeSystemContract(FakeSQS()) {
    override val anyValid = Request(GET, "/")
}
