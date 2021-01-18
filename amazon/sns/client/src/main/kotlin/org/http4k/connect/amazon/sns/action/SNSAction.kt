package org.http4k.connect.amazon.sns.action

import org.http4k.connect.Action
import org.http4k.core.ContentType.Companion.APPLICATION_FORM_URLENCODED
import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.core.Uri
import org.http4k.core.body.form
import org.http4k.core.with
import org.http4k.lens.Header.CONTENT_TYPE
import org.xml.sax.InputSource
import java.io.StringReader
import javax.xml.parsers.DocumentBuilderFactory

abstract class SNSAction<R>(private val action: String, private vararg val mappings: Pair<String, String>?) :
    Action<R> {

    override fun toRequest() =
        (listOf("Action" to action, "Version" to "2012-11-05") + mappings)
            .filterNotNull()
            .fold(
                Request(POST, uri())
                    .with(CONTENT_TYPE of APPLICATION_FORM_URLENCODED)
            ) { acc, it ->
                acc.form(it.first, it.second)
            }

    protected abstract fun uri(): Uri
}

internal fun documentBuilderFactory() =
    DocumentBuilderFactory.newInstance()
        .newDocumentBuilder()
        .apply { setEntityResolver { _, _ -> InputSource(StringReader("")) } }
