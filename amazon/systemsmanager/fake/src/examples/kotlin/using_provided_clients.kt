import org.http4k.connect.amazon.model.ParameterType
import org.http4k.connect.amazon.systemsmanager.FakeSystemsManager
import org.http4k.connect.amazon.systemsmanager.GetParameter
import org.http4k.connect.amazon.systemsmanager.PutParameter

fun main() {
    val fakeSsm = FakeSystemsManager()
    val client = fakeSsm.client()

    println(client.put(PutParameter.Request("name", "value", ParameterType.String)))
    println(client.get(GetParameter.Request("name")))
}
