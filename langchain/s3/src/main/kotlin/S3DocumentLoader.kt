import dev.forkhandles.result4k.onFailure
import dev.langchain4j.data.document.DocumentLoader
import dev.langchain4j.data.document.DocumentParser
import dev.langchain4j.data.document.DocumentSource
import dev.langchain4j.data.document.Metadata
import org.http4k.client.JavaHttpClient
import org.http4k.cloudnative.env.Environment
import org.http4k.connect.amazon.AWS_REGION
import org.http4k.connect.amazon.CredentialsProvider
import org.http4k.connect.amazon.Environment
import org.http4k.connect.amazon.s3.Http
import org.http4k.connect.amazon.s3.S3Bucket
import org.http4k.connect.amazon.s3.listObjectsV2Paginated
import org.http4k.connect.amazon.s3.model.BucketKey
import org.http4k.connect.amazon.s3.model.BucketName
import org.http4k.core.HttpHandler
import org.http4k.core.Uri
import org.http4k.filter.Payload
import java.time.Clock

class S3DocumentLoader(
    private val environment: Environment,
    private val credentialsProvider: CredentialsProvider = CredentialsProvider.Environment(environment),
    private val http: HttpHandler = JavaHttpClient(),
    private val clock: Clock = Clock.systemUTC(),
    private val overrideEndpoint: Uri? = null,
    private val forcePathStyle: Boolean = false
) {
    fun loadDocument(bucket: BucketName, key: BucketKey, parser: DocumentParser) =
        DocumentLoader.load(S3DocumentSource(bucket, key), parser)

    fun loadDocuments(bucket: BucketName, parser: DocumentParser) = loadDocuments(bucket, null, parser)

    fun loadDocuments(bucket: BucketName, prefix: String?, parser: DocumentParser) =
        s3Client(bucket)
            .listObjectsV2Paginated(prefix = prefix)
            .map {
                it.onFailure { it.reason.throwIt() }
                    .filter { !it.Key.value.endsWith("/") && (it.Size ?: 0) > 0 }
                    .map { loadDocument(bucket, it.Key, parser) }
            }
            .flatten()

    private fun s3Client(bucket: BucketName) = S3Bucket.Http(
        bucket,
        AWS_REGION(environment),
        credentialsProvider,
        http,
        clock,
        Payload.Mode.Signed,
        overrideEndpoint,
        forcePathStyle
    )

    private fun S3DocumentSource(bucket: BucketName, key: BucketKey) = object : DocumentSource {
        private val inputStream = s3Client(bucket)[key].onFailure { it.reason.throwIt() }
        override fun inputStream() = inputStream
        override fun metadata() = Metadata.from("source", "s3://$bucket/$key")
    }
}
