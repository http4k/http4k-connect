package org.http4k.connect.amazon.s3

import org.http4k.client.JavaHttpClient
import org.http4k.connect.amazon.RealAwsEnvironment
import org.http4k.connect.amazon.configAwsEnvironment
import org.http4k.connect.amazon.s3.model.BucketName
import org.http4k.filter.debug
import java.time.Duration
import java.util.UUID

class RealS3BucketTest : S3BucketContract(JavaHttpClient().debug()), RealAwsEnvironment {
    override val aws get() = configAwsEnvironment()
    override fun waitForBucketCreation() {
        Thread.sleep(10000)
    }
    override fun waitForRestore() {
        Thread.sleep(Duration.ofMinutes(5))
    }

    override val bucket: BucketName = BucketName.of(UUID.randomUUID().toString())
}

class RealS3BucketPathStyleTest : S3BucketContract(JavaHttpClient()), RealAwsEnvironment {
    override val aws get() = configAwsEnvironment()

    override fun waitForBucketCreation() {
        Thread.sleep(10000)
    }
    override fun waitForRestore() {
        Thread.sleep(Duration.ofMinutes(5))
    }

    override val bucket = BucketName.of(UUID.randomUUID().toString().replace('-', '.'))
}

class RealS3GlobalTest : S3GlobalContract(JavaHttpClient()), RealAwsEnvironment {
    override val aws get() = configAwsEnvironment()
}
