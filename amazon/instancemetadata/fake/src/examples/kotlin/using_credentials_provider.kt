import org.http4k.connect.amazon.CredentialsProvider
import org.http4k.connect.amazon.instancemetadata.Ec2InstanceProfile
import org.http4k.connect.amazon.s3.Http
import org.http4k.connect.amazon.s3.S3
import org.http4k.connect.amazon.s3.listBuckets
import org.http4k.connect.successValue

fun main() {
    // build a credentials provider that will attempt to load AWS credentials from the EC2's instance profile
    val credentialsProvider = CredentialsProvider.Ec2InstanceProfile()

    // build a client that will authorize requests with the instance profile credentials
    val s3 = S3.Http(credentialsProvider)

    // send a request
    val buckets = s3.listBuckets().successValue()
    println(buckets)
}
