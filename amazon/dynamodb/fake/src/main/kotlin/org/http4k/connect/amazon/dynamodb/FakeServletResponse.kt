package org.http4k.connect.amazon.dynamodb

import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import java.io.ByteArrayOutputStream
import java.lang.reflect.Proxy
import javax.servlet.ReadListener
import javax.servlet.ServletInputStream
import javax.servlet.ServletOutputStream
import javax.servlet.WriteListener
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class FakeServletResponse : HttpServletResponse by proxy() {

    private val output = ByteArrayOutputStream()

    private var response = Response(Status.OK)

    override fun getOutputStream() = object : ServletOutputStream() {
        override fun write(b: Int) {
            output.write(b)
        }

        override fun isReady() = true

        override fun setWriteListener(writeListener: WriteListener?) {
        }
    }

    override fun setStatus(sc: Int) {
        response = response.status(Status(sc, null))
    }

    override fun setHeader(name: String, value: String) {
        response = response.header(name, value)
    }


    fun build() = response.body(output.toString())
}

class FakeServletRequest(private val request: Request) : HttpServletRequest by proxy() {
    override fun getInputStream(): ServletInputStream =
        object : ServletInputStream() {
            override fun read() = request.body.stream.read()

            override fun isFinished() = request.body.stream.available() == 0

            override fun isReady() = true

            override fun setReadListener(readListener: ReadListener) {
            }
        }
}

inline fun <reified T> proxy(): T = Proxy.newProxyInstance(
    T::class.java.classLoader,
    arrayOf(T::class.java)
) { _, _, _ -> TODO("not implemented") } as T
