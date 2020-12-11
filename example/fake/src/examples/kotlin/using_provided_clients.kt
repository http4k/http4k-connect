import org.http4k.connect.example.EchoRequest
import org.http4k.connect.example.FakeExample

fun main() {
    val fakeExample = FakeExample()

    fakeExample.client()(EchoRequest("hello"))
}
