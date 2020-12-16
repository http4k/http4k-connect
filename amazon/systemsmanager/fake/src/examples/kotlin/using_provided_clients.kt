import org.http4k.connect.amazon.model.ParameterType
import org.http4k.connect.amazon.systemsmanager.FakeSystemsManager
import org.http4k.connect.amazon.systemsmanager.getParameter
import org.http4k.connect.amazon.systemsmanager.putParameter

fun main() {
    val fakeSsm = FakeSystemsManager()
    val client = fakeSsm.client()

    println(client.putParameter("name", "value", ParameterType.String))
    println(client.getParameter("name"))
}
