import org.http4k.connect.amazon.sqs.FakeSQS
import org.http4k.connect.amazon.sqs.sendMessage

fun main() {
    val fakeSqs = FakeSQS()

    fakeSqs.client().sendMessage("payload")
}
