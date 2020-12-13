import org.http4k.connect.example.FakeExample
import org.http4k.connect.example.action.Echo

fun main() {
    val fakeExample = FakeExample()

    fakeExample.client()(Echo("hello"))
}
