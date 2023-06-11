package org.http4k.connect.plugin

import com.google.devtools.ksp.getAllSuperTypes
import com.google.devtools.ksp.getConstructors
import com.google.devtools.ksp.symbol.ClassKind.OBJECT
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSNode
import com.google.devtools.ksp.visitor.KSEmptyVisitor
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.asTypeName
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toTypeName
import dev.forkhandles.result4k.Result4k
import org.http4k.connect.PagedAction
import org.http4k.connect.RemoteFailure
import java.util.Locale

class Http4kConnectActionVisitor(private val log: (Any?) -> Unit) :
    KSEmptyVisitor<KSClassDeclaration, Sequence<FunSpec>>() {
    override fun visitClassDeclaration(
        classDeclaration: KSClassDeclaration,
        data: KSClassDeclaration
    ): Sequence<FunSpec> {
        log("Processing " + classDeclaration.asStarProjectedType().declaration.qualifiedName!!.asString())

        return classDeclaration.getConstructors().flatMap { ctr ->
            listOfNotNull(
                generateActionExtension(classDeclaration, data, ctr),
                classDeclaration.takeIf {
                    it.getAllSuperTypes().map { it.toClassName().canonicalName }
                        .contains(PagedAction::class.qualifiedName)
                }
                    ?.let { generateActionPagination(classDeclaration, data, ctr) }
            )
        }
    }

    override fun defaultHandler(node: KSNode, data: KSClassDeclaration) = error("unsupported")
}

private fun generateActionPagination(
    actionClass: KSClassDeclaration,
    adapterClazz: KSClassDeclaration,
    ctr: KSFunctionDeclaration
) = generateExtensionFunction(
    actionClass, adapterClazz, ctr, "Paginated", CodeBlock.of(
        "return org.http4k.connect.paginated(::invoke, %T(${ctr.parameters.joinToString(", ") { it.name!!.asString() }}))",
        actionClass.asType(emptyList()).toTypeName()
    ), Sequence::class.asClassName().parameterizedBy(
        Result4k::class.asClassName().parameterizedBy(List::class.asClassName().parameterizedBy(
            actionClass.getAllSuperTypes().toList()
                .first { it.toClassName() == PagedAction::class.asClassName() }
                .arguments[1].toTypeName()
        ), RemoteFailure::class.asTypeName())
    )
)

private fun generateActionExtension(
    actionClass: KSClassDeclaration,
    adapterClazz: KSClassDeclaration,
    ctr: KSFunctionDeclaration
) = generateExtensionFunction(
    actionClass, adapterClazz, ctr, "",
    CodeBlock.of(
        when (actionClass.classKind) {
            OBJECT -> "return invoke(%T)"
            else -> "return invoke(%T(${ctr.parameters.joinToString(", ") { it.name!!.asString() }}))"
        },
        actionClass.asType(emptyList()).toTypeName()
    ),
    actionClass.getAllFunctions()
        .first { it.simpleName.getShortName() == "toResult" }.returnType!!.toTypeName()
)

private fun generateExtensionFunction(
    actionClazz: KSClassDeclaration,
    adapterClazz: KSClassDeclaration,
    ctr: KSFunctionDeclaration,
    suffix: String,
    codeBlock: CodeBlock,
    returnType: TypeName
): FunSpec {
    val baseFunction = FunSpec.builder(
        actionClazz.simpleName.asString().replaceFirstChar { it.lowercase(Locale.getDefault()) } + suffix)
        .addKdoc("@see ${actionClazz.qualifiedName!!.asString().replace('/', '.')}")
        .receiver(adapterClazz.toClassName())
        .returns(returnType)
        .addCode(codeBlock)

    ctr.parameters.forEach {
        val base = ParameterSpec.builder(it.name!!.asString(), it.type.toTypeName())
        if (it.type.resolve().isMarkedNullable) base.defaultValue(CodeBlock.of("null"))
        baseFunction.addParameter(base.build())
    }
    return baseFunction.build()
}
