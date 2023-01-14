package org.http4k.connect.plugin

import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.metadata.KotlinPoetMetadataPreview
import com.squareup.kotlinpoet.metadata.isNullable
import com.squareup.kotlinpoet.metadata.isPrivate
import com.squareup.kotlinpoet.metadata.toKmClass
import kotlinx.metadata.KmClass
import kotlinx.metadata.KmClassifier
import kotlinx.metadata.KmConstructor
import org.http4k.connect.Http4kConnectAction
import org.http4k.connect.Http4kConnectAdapter
import org.http4k.connect.PagedAction
import org.http4k.connect.plugin.Http4kConnectProcessor.Companion.KSP_KOTLIN_GENERATED_OPTION_NAME
import java.io.File
import java.util.Locale.getDefault
import javax.annotation.processing.RoundEnvironment
import javax.annotation.processing.SupportedAnnotationTypes
import javax.annotation.processing.SupportedOptions
import javax.annotation.processing.SupportedSourceVersion
import javax.lang.model.SourceVersion.RELEASE_8
import javax.lang.model.element.TypeElement

@KotlinPoetMetadataPreview
@SupportedSourceVersion(RELEASE_8)
@SupportedAnnotationTypes("org.http4k.connect.Http4kConnectAdapter")
@SupportedOptions(KSP_KOTLIN_GENERATED_OPTION_NAME)
class Http4kConnectAdapterProcessor : Http4kConnectProcessor() {

    override fun generate(annotations: Set<TypeElement>, roundEnv: RoundEnvironment, outputDir: File): Boolean {
        roundEnv.annotated<Http4kConnectAdapter>()
            .map { it.toKmClass() }
            .map { it.generateActionExtensionsFor(roundEnv) }
            .forEach { it.writeTo(outputDir) }
        return true
    }

    private fun KmClass.generateActionExtensionsFor(roundEnv: RoundEnvironment): FileSpec {
        val (packageName, className) = explodeName()

        val actionVP = functions.find { it.name == "invoke" }!!.valueParameters.first()
        val actionType = processingEnv.elementUtils.getTypeElement(
            (actionVP.type.classifier as KmClassifier.Class).name.replace('/', '.')
        )

        val functions = roundEnv.annotated<Http4kConnectAction>()
            .filter {
                superTypesOf(it.asType())
                    .map(::rawType)
                    .contains(rawType(actionType.asType()))
            }
            .map { it.toKmClass() }
            .flatMap { generateActionFunctions(this, it) }

        return FileSpec.builder(packageName, className.lowercase(getDefault()) + "Extensions")
            .apply { functions.forEach(::addFunction) }
            .build()
    }
}

@KotlinPoetMetadataPreview
private fun generateActionFunctions(adapterClazz: KmClass, actionClazz: KmClass): List<FunSpec> =
    actionClazz.constructors
        .filterNot { it.flags.isPrivate }
        .flatMap { ctr ->
            listOfNotNull(
                generateStandardActionFunctionFor(actionClazz, adapterClazz, ctr),
                actionClazz.takeIf { it.isSubTypeOf(PagedAction::class) }
                    ?.let { generatePagedActionFunctionFor(actionClazz, adapterClazz, ctr) }
            )
        }

@KotlinPoetMetadataPreview
private fun generatePagedActionFunctionFor(
    actionClazz: KmClass,
    adapterClazz: KmClass,
    ctr: KmConstructor
) = generateExtensionFunction(
    actionClazz, adapterClazz, ctr, "Paginated", CodeBlock.of(
        "return org.http4k.connect.paginated(::invoke, %T(${ctr.valueParameters.joinToString(", ") { it.name }}))",
        actionClazz.name.asClassName()
    )
)

@KotlinPoetMetadataPreview
private fun generateStandardActionFunctionFor(
    actionClazz: KmClass,
    adapterClazz: KmClass,
    ctr: KmConstructor
) = generateExtensionFunction(
    actionClazz, adapterClazz, ctr, "", CodeBlock.of(
        "return invoke(%T(${ctr.valueParameters.joinToString(", ") { it.name }}))",
        actionClazz.name.asClassName()
    )
)

@KotlinPoetMetadataPreview
private fun generateExtensionFunction(
    actionClazz: KmClass,
    adapterClazz: KmClass,
    ctr: KmConstructor,
    suffix: String,
    codeBlock: CodeBlock
): FunSpec {
    val baseFunction = FunSpec.builder(actionClazz.name.name().replaceFirstChar { it.lowercase(getDefault()) } + suffix)
        .addKdoc("@see ${actionClazz.name.replace('/', '.')}")
        .receiver(adapterClazz.name.asClassName())
        .addCode(codeBlock)

    ctr.valueParameters.forEach {
        val base = ParameterSpec.builder(it.name, it.type.generifiedType())
        if (it.type.isNullable) base.defaultValue(CodeBlock.of("null"))
        baseFunction.addParameter(base.build())
    }
    return baseFunction.build()
}
