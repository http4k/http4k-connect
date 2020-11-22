import org.http4k.core.HttpHandler
import org.http4k.core.then
import org.http4k.filter.DebuggingFilters

@Suppress("unused")
fun HttpHandler.debug() = DebuggingFilters.PrintRequestAndResponse().then(this)
