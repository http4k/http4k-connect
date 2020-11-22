package org.http4k.connect.amazon.model

import org.xml.sax.InputSource
import java.io.StringReader
import javax.xml.parsers.DocumentBuilderFactory

data class BucketName(val name: String) {
    override fun toString() = name
}

data class BucketKey(val value: String) {
    override fun toString() = value
}

internal val documentBuilderFactory by lazy {
    DocumentBuilderFactory.newInstance()
        .newDocumentBuilder()
        .apply { setEntityResolver { _, _ -> InputSource(StringReader("")) } }
}
