package org.http4k.connect.amazon.sqs.action

import org.http4k.connect.Action
import org.w3c.dom.Document
import org.xml.sax.InputSource
import java.io.StringReader
import javax.xml.parsers.DocumentBuilderFactory

interface SQSAction<R> : Action<R>

internal val documentBuilderFactory by lazy {
    DocumentBuilderFactory.newInstance()
        .newDocumentBuilder()
        .apply { setEntityResolver { _, _ -> InputSource(StringReader("")) } }
}

internal fun Document.text(name: String) = getElementsByTagName(name).item(0).textContent.trim()
