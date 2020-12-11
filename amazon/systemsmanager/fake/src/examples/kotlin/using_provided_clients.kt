import org.http4k.connect.amazon.model.ParameterType
import org.http4k.connect.amazon.systemsmanager.FakeSystemsManager
import org.http4k.connect.amazon.systemsmanager.GetParameterRequest
import org.http4k.connect.amazon.systemsmanager.PutParameterRequest

fun main() {
    val fakeSsm = FakeSystemsManager()
    val client = fakeSsm.client()

    println(client(PutParameterRequest("name", "value", ParameterType.String)))
    println(client(GetParameterRequest("name")))
}
