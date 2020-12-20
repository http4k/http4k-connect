import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.http4k.format.ConfigurableMoshi
import org.junit.jupiter.api.Test

abstract class SystemMoshiTest(private val moshi: ConfigurableMoshi,
                               vararg actions: Any
) {
    private val obj = actions.toList()

    @Test
    fun `can roundtrip all objects`() {
        obj.forEach {
            assertThat(moshi.asA(moshi.asFormatString(it), it::class), equalTo(it))
        }
    }
}
