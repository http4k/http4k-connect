import org.http4k.connect.amazon.model.AwsAccount
import org.http4k.connect.amazon.model.QueueName
import org.http4k.connect.amazon.sqs.FakeSQS
import org.http4k.connect.amazon.sqs.sendMessage

fun main() {
    val fakeSqs = FakeSQS()

    fakeSqs.client().sendMessage(AwsAccount.of("000000001"),
        QueueName.of("myqueue"), "payload")
}
