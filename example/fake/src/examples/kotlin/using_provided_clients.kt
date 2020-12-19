import org.http4k.connect.example.FakeExample
import org.http4k.connect.example.echo

fun main() {
    val fakeExample = FakeExample()

    fakeExample.client().echo("hello")
}
