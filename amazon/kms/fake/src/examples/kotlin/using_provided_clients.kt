import org.http4k.connect.amazon.systemsmanager.FakeKMS

fun main() {
    val fakeSsm = FakeKMS()
    val client = fakeSsm.client()

}
