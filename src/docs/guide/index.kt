package guide

import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Result
import dev.forkhandles.result4k.Success
import dev.forkhandles.result4k.map
import org.http4k.client.JavaHttpClient
import org.http4k.connect.RemoteFailure
import org.http4k.connect.amazon.s3.Http
import org.http4k.connect.amazon.s3.S3
import org.http4k.connect.amazon.s3.S3Action
import org.http4k.connect.amazon.s3.action.BucketList
import org.http4k.connect.amazon.s3.listBuckets
import org.http4k.connect.amazon.s3.model.BucketName
import org.http4k.connect.asRemoteFailure
import org.http4k.connect.orThrow
import org.http4k.core.HttpHandler
import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Uri
import org.http4k.core.then
import org.http4k.filter.ClientFilters.SetBaseUriFrom





    val s3 = S3.Http(http = JavaHttpClient())

    val buckets: Result<BucketList, RemoteFailure> = s3.listBuckets()

    val bucketNames: List<BucketName> = buckets.map { it.items }.orThrow()





    data class InvertBucket(val bucketName: BucketName) : S3Action<BucketName> {
        override fun toRequest() = Request(POST, Uri.of("/${bucketName}/invert"))

        override fun toResult(response: Response) =
            if(response.status.successful) Success(BucketName.of(response.bodyString()))
            else Failure(asRemoteFailure(response))
    }

    fun S3.invertBucket(bucketName: BucketName) = invoke(InvertBucket(bucketName))



    val a = bucketNames.forEach { println(it) }
    val inverted = s3.invertBucket(BucketName.of("my-bucket")).orThrow()




interface APIAction<R> {
        fun toRequest(): Request
        fun toResult(response: Response): Result<R, RemoteFailure>
    }



    data class Reverse(val value: String) : APIAction<String> {
        override fun toRequest() = Request(POST, "/reverse").body(value)

        override fun toResult(response: Response): Result<String, RemoteFailure> =
            Success(response.bodyString())
    }

    fun API.reverse(value: String) = this(Reverse(value))


    class API(rawHttp: HttpHandler) {
        private val transport = SetBaseUriFrom(Uri.of("https://api.com"))
            .then(rawHttp)

        operator fun <R> invoke(action: APIAction<R>): Result<R, RemoteFailure> =
            action.toResult(transport(action.toRequest()))
    }



    val api = API(JavaHttpClient())

    val result: Result<String, RemoteFailure> = api.reverse("hello")



    val b = result.map { println(it) }







