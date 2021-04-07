package org.http4k.connect.amazon.s3

import org.http4k.aws.AwsCredentials
import org.http4k.connect.ChaosFake
import org.http4k.connect.amazon.core.model.Region
import org.http4k.connect.amazon.s3.model.BucketName
import org.http4k.connect.storage.InMemory
import org.http4k.connect.storage.Storage
import org.http4k.core.Request
import org.http4k.routing.asRouter
import org.http4k.routing.bind
import org.http4k.routing.routes
import java.time.Clock

/**
 * Global S3 operations (manage buckets)
 */
class FakeS3(
    val buckets: Storage<Unit> = Storage.InMemory(),
    val bucketContent: Storage<BucketKeyContent> = Storage.InMemory(),
    private val clock: Clock = Clock.systemUTC()
) : ChaosFake() {

    private val isS3 = { it: Request -> it.subdomain(buckets) == "s3" }.asRouter()
    private val isBucket = { it: Request -> it.subdomain(buckets) != "s3" }.asRouter()

    override val app = routes(
        isS3 bind routes(
            globalListObjectsV2(buckets, bucketContent),
            globalPutBucket(buckets),
            globalListBuckets(buckets)
        ),
        isBucket bind routes(
            copyKey(buckets, bucketContent, clock),
            bucketGetKey(buckets, bucketContent),
            bucketPutKey(buckets, bucketContent, clock),
            bucketDeleteKey(buckets, bucketContent),
            bucketPutBucket(buckets),
            bucketDeleteBucket(buckets),
            bucketListObjectsV2(buckets, bucketContent)
        )
    )

    /**
     * Convenience function to get an S3 client for global operations
     */
    fun s3Client() = S3.Http({ AwsCredentials("accessKey", "secret") }, this, clock)

    /**
     * Convenience function to get an S3 client for bucket operations
     */
    fun s3BucketClient(name: BucketName, region: Region) = S3Bucket.Http(
        name,
        region,
        { AwsCredentials("accessKey", "secret") }, this, clock
    )
}

fun main() {
    FakeS3().start()
}
