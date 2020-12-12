import org.http4k.connect.example.Echo
import org.http4k.connect.example.FakeExample

fun main() {
    val fakeExample = FakeExample()

    fakeExample.client()(Echo("hello"))
}
