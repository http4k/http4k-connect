package org.http4k.connect.amazon.eventbridge

import org.http4k.client.JavaHttpClient
import org.http4k.connect.amazon.RealAwsEnvironment
import org.http4k.connect.amazon.configAwsEnvironment

class RealEventBridgeTest : EventBridgeContract(), RealAwsEnvironment 
