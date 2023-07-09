package org.http4k.connect.amazon.eventbridge

import org.http4k.connect.amazon.fakeAwsEnvironment

class FakeEventBridgeTest : EventBridgeContract(FakeEventBridge()) {
    override val aws = fakeAwsEnvironment
}
