package org.http4k.connect.amazon.model

import dev.forkhandles.values.StringValue
import dev.forkhandles.values.StringValueFactory
import org.xml.sax.InputSource
import java.io.StringReader
import javax.xml.parsers.DocumentBuilderFactory

class BucketName private constructor(value: String) : StringValue(value) {
    companion object : StringValueFactory<BucketName>(::BucketName, String::isNotEmpty)
}

class BucketKey private constructor(value: String) : StringValue(value) {
    companion object : StringValueFactory<BucketKey>(::BucketKey, String::isNotEmpty)
}

internal val documentBuilderFactory by lazy {
    DocumentBuilderFactory.newInstance()
        .newDocumentBuilder()
        .apply { setEntityResolver { _, _ -> InputSource(StringReader("")) } }
}
