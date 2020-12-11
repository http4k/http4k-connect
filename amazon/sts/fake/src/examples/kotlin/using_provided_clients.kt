import org.http4k.connect.amazon.model.ARN
import org.http4k.connect.amazon.model.AwsAccount
import org.http4k.connect.amazon.model.AwsService
import org.http4k.connect.amazon.model.Region
import org.http4k.connect.amazon.sts.AssumeRole
import org.http4k.connect.amazon.sts.FakeSTS
import java.util.UUID

fun main() {
    val fakeSts = FakeSTS()

    fakeSts.client().assumeRole(
        AssumeRole.Request(
            ARN.of(
                Region.of("ldn-north-1"), AwsService.of("kms"), "key", "resource", AwsAccount.of("0")
            ), UUID.randomUUID().toString()
        )
    )
}
