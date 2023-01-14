package org.http4k.connect.plugin

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSNode
import com.google.devtools.ksp.symbol.KSTypeReference
import com.google.devtools.ksp.visitor.KSEmptyVisitor

class Http4kConnectActionVisitor(private val log: (Any?) -> Unit) : KSEmptyVisitor<KSTypeReference, Unit>() {
    override fun visitClassDeclaration(actionClass: KSClassDeclaration, actionType: KSTypeReference) {
        log("PROCESSING " + actionClass.asStarProjectedType().declaration.qualifiedName)
        log("PROCESSING " + actionClass.superTypes.toList().map { it.element })

        if (actionClass.superTypes.contains(actionType)) {
            log("GENERATING ACTION FOR " + actionClass.simpleName)
        }
    }

    override fun defaultHandler(node: KSNode, data: KSTypeReference) {
    }
}
