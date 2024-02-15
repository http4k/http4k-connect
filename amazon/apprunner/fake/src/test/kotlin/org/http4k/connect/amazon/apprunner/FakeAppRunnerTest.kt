package org.http4k.connect.amazon.apprunner

import org.http4k.connect.amazon.fakeAwsEnvironment

class FakeAppRunnerTest : AppRunnerContract(FakeAppRunner()) {
    override val aws = fakeAwsEnvironment
}
