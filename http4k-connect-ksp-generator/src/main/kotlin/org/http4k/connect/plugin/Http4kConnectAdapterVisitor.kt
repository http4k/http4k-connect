package org.http4k.connect.plugin

import com.google.devtools.ksp.getAllSuperTypes
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSNode
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.visitor.KSEmptyVisitor
import com.squareup.kotlinpoet.FileSpec
import java.util.Locale

class Http4kConnectAdapterVisitor(private val log: (Any?) -> Unit) : KSEmptyVisitor<List<KSAnnotated>, FileSpec>() {
    override fun visitClassDeclaration(adapterClass: KSClassDeclaration, actions: List<KSAnnotated>): FileSpec {
        log(
            "Processing http4k-connect adapter: " + adapterClass.simpleName!!.asString() +
                " with action type: ${adapterClass.http4kConnectActionType}"
        )

        return FileSpec.builder(
            adapterClass.packageName.asString(),
            adapterClass.simpleName.asString().lowercase(Locale.getDefault()) + "Extensions"
        )
            .apply {
                actions.filterForActionsOf(adapterClass)
                    .flatMap { it.accept(Http4kConnectActionVisitor(log), adapterClass) }
                    .forEach(::addFunction)
            }
            .build()

    }

    override fun defaultHandler(node: KSNode, data: List<KSAnnotated>): FileSpec {
        error("unsupported")
    }
}

private val KSClassDeclaration.http4kConnectActionType
    get() = getAllFunctions()
        .first { it.simpleName.getShortName() == "invoke" }
        .parameters.first().type

fun List<KSAnnotated>.filterForActionsOf(adapter: KSClassDeclaration) =
    filterIsInstance<KSClassDeclaration>()
        .filter {
            it.getAllSuperTypes().map(KSType::starProjection)
                .contains(adapter.http4kConnectActionType.resolve().starProjection())
        }
