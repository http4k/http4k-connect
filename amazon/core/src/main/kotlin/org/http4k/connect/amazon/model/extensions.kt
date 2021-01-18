package org.http4k.connect.amazon.model

import org.http4k.core.Response
import org.w3c.dom.Document
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import org.xml.sax.InputSource
import java.io.StringReader
import javax.xml.parsers.DocumentBuilderFactory

fun Response.xmlDoc(): Document = documentBuilderFactory().parse(body.stream)

private fun documentBuilderFactory() =
    DocumentBuilderFactory.newInstance()
        .newDocumentBuilder()
        .apply { setEntityResolver { _, _ -> InputSource(StringReader("")) } }

fun Document.text(name: String) = textOptional(name)!!

fun Document.textOptional(name: String) = getElementsByTagName(name).item(0)?.text()

fun NodeList.sequenceOfNodes(onlyChildrenNamed: String? = null): Sequence<Node> {
    var i = 0
    val baseSequence = generateSequence { i.let { if (it == length) null else item(it) }.also { i++ } }
    return onlyChildrenNamed?.let { baseSequence.filter { it.nodeName == onlyChildrenNamed } } ?: baseSequence
}

fun Node.firstChild(name: String) = children(name).first()
fun Node.children(name: String) = childNodes.sequenceOfNodes(name)
fun Node.text() = textContent.trim()

