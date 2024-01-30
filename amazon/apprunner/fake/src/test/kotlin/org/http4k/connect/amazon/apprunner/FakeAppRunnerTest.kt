package org.http4k.connect.amazon.apprunner

import org.http4k.connect.amazon.fakeAwsEnvironment
import org.http4k.connect.amazon.lambda.AppRunnerContract

class FakeAppRunnerTest : AppRunnerContract(FakeAppRunner()) {
    override val aws = fakeAwsEnvironment
}

