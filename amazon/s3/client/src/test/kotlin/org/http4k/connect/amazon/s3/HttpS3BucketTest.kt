package org.http4k.connect.amazon.s3

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.http4k.connect.amazon.core.model.Region
import org.http4k.connect.amazon.fakeAwsEnvironment
import org.http4k.connect.amazon.s3.model.BucketKey
import org.http4k.connect.amazon.s3.model.BucketName
import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK
import org.http4k.core.Uri
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

class HttpS3BucketTest {

    class UriMatcher : HttpHandler {

        var requestUri: Uri? = null

        override fun invoke(request: Request): Response {
            requestUri = request.uri
            return Response(OK)
        }
    }

    companion object {
        @JvmStatic
        fun requestUriSource(): List<Arguments> {
            return listOf(
                Arguments.of(
                    BucketName.of("host-style-bucket"),
                    false,
                    Uri.of("https://host-style-bucket.s3.us-east-1.amazonaws.com/key")
                ),
                Arguments.of(
                    BucketName.of("path.style.bucket"),
                    false,
                    Uri.of("https://s3.us-east-1.amazonaws.com/path.style.bucket/key")
                ),
                Arguments.of(
                    BucketName.of("host-style-bucket"),
                    true,
                    Uri.of("https://s3.us-east-1.amazonaws.com/host-style-bucket/key")
                ),
            )
        }
    }

    @ParameterizedTest
    @MethodSource("requestUriSource")
    fun `builds correct request uri`(bucketName: BucketName, forcePathStyle: Boolean, expectedUri: Uri) {
        // given
        val uriMatcher = UriMatcher()
        val bucket = S3Bucket.Http(
            bucketName = bucketName,
            bucketRegion = Region.US_EAST_1,
            credentialsProvider = { fakeAwsEnvironment.credentials },
            http = uriMatcher,
            forcePathStyle = forcePathStyle
        )

        // when
        bucket[BucketKey.of("key")]

        // then
        assertThat(
            uriMatcher.requestUri,
            equalTo(expectedUri)
        )
    }
}
