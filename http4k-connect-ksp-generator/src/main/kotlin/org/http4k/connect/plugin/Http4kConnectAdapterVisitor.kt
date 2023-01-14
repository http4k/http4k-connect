package org.http4k.connect.plugin

import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSNode
import com.google.devtools.ksp.visitor.KSEmptyVisitor

class Http4kConnectAdapterVisitor(private val log: (Any?) -> Unit) : KSEmptyVisitor<List<KSAnnotated>, Unit>() {
    override fun visitClassDeclaration(adapterClass: KSClassDeclaration, actions: List<KSAnnotated>) {
        log("PROCESSING ADAPTER " + adapterClass.simpleName)
        actions.forEach { it.accept(Http4kConnectActionVisitor(log), adapterClass.http4kConnectActionType) }
    }

    override fun defaultHandler(node: KSNode, data: List<KSAnnotated>) {
    }
}

private val KSClassDeclaration.http4kConnectActionType
    get() = getAllFunctions()
        .first { it.simpleName.getShortName() == "invoke" }
        .parameters.first().type
