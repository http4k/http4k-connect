package org.http4k.connect.plugin

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import dev.forkhandles.result4k.Result
import org.http4k.connect.Http4kConnectAdapter
import org.http4k.connect.Paged
import org.http4k.connect.RemoteFailure
import org.http4k.connect.plugin.bar.BarAction
import org.http4k.connect.plugin.foo.FooAction
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.format.ConfigurableMoshi
import org.http4k.format.asConfigurable
import se.ansman.kotshi.JsonSerializable
import se.ansman.kotshi.KotshiJsonAdapterFactory

@Http4kConnectAdapter
interface TestAdapter {
    operator fun <R> invoke(action: FooAction<R>): Result<R, RemoteFailure>
    operator fun <R> invoke(action: BarAction<R>): Result<R, RemoteFailure>

    companion object
}

fun TestAdapter.Companion.Impl() = object : TestAdapter {
    override fun <R> invoke(action: FooAction<R>) = action.toResult(Response(Status.OK))
    override fun <R> invoke(action: BarAction<R>) = action.toResult(Response(Status.OK))
}

object TestMoshi : ConfigurableMoshi(
    Moshi.Builder()
        .add(TestJsonFactory)
        .asConfigurable()
        .done()
)

@JsonSerializable
data class TestBean(val value: String)

@KotshiJsonAdapterFactory
object TestJsonFactory : JsonAdapter.Factory by KotshiTestJsonFactory

data class TestPaged(val token: String) : Paged<String, String> {
    override fun token() = token

    override val items = emptyList<String>()
}
