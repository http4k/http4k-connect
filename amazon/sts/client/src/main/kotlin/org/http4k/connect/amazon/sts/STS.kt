package org.http4k.connect.amazon.sts

import dev.forkhandles.result4k.Result
import org.http4k.connect.Action
import org.http4k.connect.RemoteFailure
import org.xml.sax.InputSource
import java.io.StringReader
import javax.xml.parsers.DocumentBuilderFactory

/**
 * Docs: https://docs.aws.amazon.com/STS/latest/APIReference/welcome.html
 */
interface STSAction<R> : Action<R>

interface STS {
    operator fun <R> invoke(request: STSAction<R>): Result<R, RemoteFailure>

    companion object
}

internal val documentBuilderFactory by lazy {
    DocumentBuilderFactory.newInstance()
        .newDocumentBuilder()
        .apply { setEntityResolver { _, _ -> InputSource(StringReader("")) } }
}
