package org.http4k.connect.amazon.containercredentials

import org.http4k.connect.amazon.containerCredentials.ContainerCredentialsContract
import org.http4k.connect.amazon.fakeAwsEnvironment
import org.http4k.core.Uri

class FakeContainerCredentialsTest : ContainerCredentialsContract(FakeContainerCredentials()) {
    override val relativePathUri = Uri.of("/foobar")
    override val aws = fakeAwsEnvironment
}
