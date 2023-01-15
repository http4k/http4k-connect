package org.http4k.connect.plugin

import com.google.devtools.ksp.getAllSuperTypes
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSNode
import com.google.devtools.ksp.visitor.KSEmptyVisitor
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toTypeName
import org.http4k.connect.PagedAction
import java.util.Locale

class Http4kConnectActionVisitor(private val log: (Any?) -> Unit) :
    KSEmptyVisitor<KSClassDeclaration, List<FunSpec>>() {
    override fun visitClassDeclaration(
        actionClass: KSClassDeclaration,
        adapterClazz: KSClassDeclaration
    ): List<FunSpec> {
        log("Processing " + actionClass.asStarProjectedType().declaration.qualifiedName!!.asString())

        val ctr = actionClass.primaryConstructor!!

        return listOfNotNull(
            generateActionExtension(actionClass, adapterClazz, ctr),
            actionClass.takeIf {
                it.getAllSuperTypes().map { it.toClassName().canonicalName }
                    .contains(PagedAction::class.qualifiedName)
            }
                ?.let { generateActionPagination(actionClass, adapterClazz, ctr) }
        )
    }

    private fun generateActionPagination(
        actionClass: KSClassDeclaration,
        adapterClazz: KSClassDeclaration,
        ctr: KSFunctionDeclaration
    ) = generateExtensionFunction(
        actionClass, adapterClazz, ctr, "Paginated", CodeBlock.of(
            "return org.http4k.connect.paginated(::invoke, %T(${ctr.parameters.joinToString(", ") { it.name!!.asString() }}))",
            actionClass.asType(emptyList()).toTypeName()
        )
    )

    private fun generateActionExtension(
        actionClass: KSClassDeclaration,
        adapterClazz: KSClassDeclaration,
        ctr: KSFunctionDeclaration
    ) = generateExtensionFunction(
        actionClass, adapterClazz, ctr, "", CodeBlock.of(
            "return invoke(%T(${ctr.parameters.joinToString(", ") { it.name!!.asString() }}))",
            actionClass.asType(emptyList()).toTypeName()
        )
    )

    override fun defaultHandler(node: KSNode, data: KSClassDeclaration): List<FunSpec> {
        error("unsupported")
    }

    private fun generateExtensionFunction(
        actionClazz: KSClassDeclaration,
        adapterClazz: KSClassDeclaration,
        ctr: KSFunctionDeclaration,
        suffix: String,
        codeBlock: CodeBlock
    ): FunSpec {
        val baseFunction = FunSpec.builder(
            actionClazz.simpleName.asString().replaceFirstChar { it.lowercase(Locale.getDefault()) } + suffix)
            .addKdoc("@see ${actionClazz.simpleName.asString().replace('/', '.')}")
            .receiver(adapterClazz.simpleName.asString().asClassName())
            .addCode(codeBlock)

        ctr.parameters.forEach {
            val base = ParameterSpec.builder(it.name!!.asString(), it.type.toTypeName())
            if (it.type.resolve().isMarkedNullable) base.defaultValue(CodeBlock.of("null"))
            baseFunction.addParameter(base.build())
        }
        return baseFunction.build()
    }

}
