import org.http4k.connect.amazon.model.ARN
import org.http4k.connect.amazon.model.AwsAccount
import org.http4k.connect.amazon.model.AwsService
import org.http4k.connect.amazon.model.Region
import org.http4k.connect.amazon.sts.FakeSTS
import org.http4k.connect.amazon.sts.assumeRole
import java.util.UUID

fun main() {
    val fakeSts = FakeSTS()

    fakeSts.client().assumeRole(
            ARN.of(
                AwsService.of("kms"), Region.of("ldn-north-1"), AwsAccount.of("0"), "key", "resource"
            ), UUID.randomUUID().toString()
    )
}
