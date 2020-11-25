import org.http4k.connect.amazon.systemsmanager.FakeSystemsManager

fun main() {
    val fakeSsm = FakeSystemsManager()
    val client = fakeSsm.client()

}
