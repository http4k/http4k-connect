import org.http4k.connect.amazon.sqs.FakeSQS
import org.http4k.connect.amazon.sqs.action.SendMessage

fun main() {
    val fakeSqs = FakeSQS()

    fakeSqs.client()(SendMessage("payload"))
}
