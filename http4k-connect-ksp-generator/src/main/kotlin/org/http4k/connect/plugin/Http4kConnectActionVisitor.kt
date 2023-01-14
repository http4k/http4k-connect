package org.http4k.connect.plugin

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSNode
import com.google.devtools.ksp.visitor.KSEmptyVisitor

class Http4kConnectActionVisitor(private val log: (Any?) -> Unit) : KSEmptyVisitor<Unit, Unit>() {
    override fun visitClassDeclaration(actionClass: KSClassDeclaration, data: Unit) {
        log("PROCESSING " + actionClass.asStarProjectedType().declaration.qualifiedName)
        log("PROCESSING " + actionClass.superTypes.toList().map { it.element })
    }

    override fun defaultHandler(node: KSNode, data: Unit) {
    }
}
