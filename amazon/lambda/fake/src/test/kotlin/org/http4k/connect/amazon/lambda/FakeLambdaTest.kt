package org.http4k.connect.amazon.lambda

import org.http4k.connect.amazon.fakeAwsEnvironment

class FakeLambdaTest : LambdaContract(FakeLambda(functions)) {
    override val aws = fakeAwsEnvironment
}

