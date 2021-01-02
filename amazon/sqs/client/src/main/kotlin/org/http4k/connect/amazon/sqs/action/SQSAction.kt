package org.http4k.connect.amazon.sqs.action

import org.http4k.connect.Action
import org.http4k.core.ContentType.Companion.APPLICATION_FORM_URLENCODED
import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.core.Uri
import org.http4k.core.body.form
import org.http4k.core.with
import org.http4k.lens.Header.CONTENT_TYPE
import org.w3c.dom.Document
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import org.xml.sax.InputSource
import java.io.StringReader
import javax.xml.parsers.DocumentBuilderFactory

abstract class SQSAction<R>(private val action: String, private vararg val mappings: Pair<String, String>?) : Action<R> {
    override fun toRequest() =
        (listOf("Action" to action, "Version" to "2012-11-05") + mappings)
            .filterNotNull()
            .fold(Request(POST, uri())
                .with(CONTENT_TYPE of APPLICATION_FORM_URLENCODED)) { acc, it ->
                acc.form(it.first, it.second)
            }

    protected abstract fun uri(): Uri
}

internal fun documentBuilderFactory() =
    DocumentBuilderFactory.newInstance()
        .newDocumentBuilder()
        .apply { setEntityResolver { _, _ -> InputSource(StringReader("")) } }

internal fun Document.text(name: String) = getElementsByTagName(name).item(0).textContent.trim()

internal fun NodeList.sequenceOfNodes(onlyChildrenNamed: String? = null): Sequence<Node> {
    var i = 0
    val baseSequence = generateSequence { i.let { if (it == length) null else item(it) }.also { i++ } }
    return onlyChildrenNamed?.let { baseSequence.filter { it.nodeName == onlyChildrenNamed } } ?: baseSequence
}

internal fun Node.firstChild(name: String) = children(name).first()
internal fun Node.children(name: String) = childNodes.sequenceOfNodes(name)
