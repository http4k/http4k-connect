package org.http4k.connect.amazon.sts.action

import org.http4k.connect.Action
import org.xml.sax.InputSource
import java.io.StringReader
import javax.xml.parsers.DocumentBuilderFactory

interface STSAction<R> : Action<R>

internal fun documentBuilderFactory() =
    DocumentBuilderFactory.newInstance()
        .newDocumentBuilder()
        .apply { setEntityResolver { _, _ -> InputSource(StringReader("")) } }
