package org.http4k.connect.amazon.containercredentials

import org.http4k.connect.amazon.containerCredentials.ContainerCredentialsContract
import org.http4k.connect.amazon.fakeAwsEnvironment
import org.http4k.core.Uri

class FakeContainerCredentialsTest : ContainerCredentialsContract(FakeContainerCredentials()) {
    override val fullUri = Uri.of("http://localhost:80/foobar")
    override val aws = fakeAwsEnvironment
}
