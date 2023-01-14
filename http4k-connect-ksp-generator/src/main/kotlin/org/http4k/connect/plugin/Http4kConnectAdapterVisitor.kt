package org.http4k.connect.plugin

import com.google.devtools.ksp.getAllSuperTypes
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSNode
import com.google.devtools.ksp.visitor.KSEmptyVisitor

class Http4kConnectAdapterVisitor(private val log: (Any?) -> Unit) : KSEmptyVisitor<List<KSAnnotated>, Unit>() {
    override fun visitClassDeclaration(adapterClass: KSClassDeclaration, actions: List<KSAnnotated>) {
        log("Processing http4k-connect adapter: " + adapterClass.qualifiedName!!.asString() + " with action type: ${adapterClass.http4kConnectActionType}")
        actions.filterForActionsOf(adapterClass)
            .forEach { it.accept(Http4kConnectActionVisitor(log), Unit) }
    }

    override fun defaultHandler(node: KSNode, data: List<KSAnnotated>) {
    }
}

private val KSClassDeclaration.http4kConnectActionType
    get() = getAllFunctions()
        .first { it.simpleName.getShortName() == "invoke" }
        .parameters.first().type

fun List<KSAnnotated>.filterForActionsOf(adapter: KSClassDeclaration) =
    filterIsInstance<KSClassDeclaration>()
        .filter {
            it.getAllSuperTypes().map { it.starProjection() }
                .contains(adapter.http4kConnectActionType.resolve().starProjection())
        }
