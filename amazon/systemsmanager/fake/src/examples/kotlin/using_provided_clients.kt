import org.http4k.connect.amazon.model.ParameterType
import org.http4k.connect.amazon.systemsmanager.FakeSystemsManager
import org.http4k.connect.amazon.systemsmanager.action.GetParameter
import org.http4k.connect.amazon.systemsmanager.action.PutParameter

fun main() {
    val fakeSsm = FakeSystemsManager()
    val client = fakeSsm.client()

    println(client(PutParameter("name", "value", ParameterType.String)))
    println(client(GetParameter("name")))
}
