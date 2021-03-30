package org.http4k.connect.plugin

import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.metadata.ImmutableKmClass
import com.squareup.kotlinpoet.metadata.ImmutableKmConstructor
import com.squareup.kotlinpoet.metadata.KotlinPoetMetadataPreview
import com.squareup.kotlinpoet.metadata.isNullable
import com.squareup.kotlinpoet.metadata.isPrivate
import com.squareup.kotlinpoet.metadata.toImmutableKmClass
import kotlinx.metadata.KmClassifier
import org.http4k.connect.Http4kConnectAction
import org.http4k.connect.Http4kConnectAdapter
import org.http4k.connect.PagedAction
import org.http4k.connect.plugin.Http4kConnectProcessor.Companion.KAPT_KOTLIN_GENERATED_OPTION_NAME
import java.io.File
import javax.annotation.processing.RoundEnvironment
import javax.annotation.processing.SupportedAnnotationTypes
import javax.annotation.processing.SupportedOptions
import javax.annotation.processing.SupportedSourceVersion
import javax.lang.model.SourceVersion.RELEASE_8
import javax.lang.model.element.TypeElement

@KotlinPoetMetadataPreview
@SupportedSourceVersion(RELEASE_8)
@SupportedAnnotationTypes("org.http4k.connect.Http4kConnectAdapter")
@SupportedOptions(KAPT_KOTLIN_GENERATED_OPTION_NAME)
class Http4kConnectAdapterProcessor : Http4kConnectProcessor() {

    override fun generate(annotations: Set<TypeElement>, roundEnv: RoundEnvironment, outputDir: File): Boolean {
        roundEnv.annotated<Http4kConnectAdapter>()
            .map { it.toImmutableKmClass() }
            .map { it.generateActionExtensionsFor(roundEnv) }
            .forEach { it.writeTo(outputDir) }
        return true
    }

    private fun ImmutableKmClass.generateActionExtensionsFor(roundEnv: RoundEnvironment): FileSpec {
        val (packageName, className) = explodeName()

        val actionVP = functions.find { it.name == "invoke" }!!.valueParameters.first()
        val actionType = processingEnv.elementUtils.getTypeElement(
            (actionVP.type!!.classifier as KmClassifier.Class).name.replace('/', '.')
        )

        val functions = roundEnv.annotated<Http4kConnectAction>()
            .filter {
                superTypesOf(it.asType())
                    .map(::rawType)
                    .contains(rawType(actionType.asType()))
            }
            .map { it.toImmutableKmClass() }
            .flatMap { generateActionFunctions(this, it) }

        return FileSpec.builder(packageName, className.toLowerCase() + "Extensions")
            .apply { functions.forEach(::addFunction) }
            .build()
    }
}

@KotlinPoetMetadataPreview
private fun generateActionFunctions(adapterClazz: ImmutableKmClass, actionClazz: ImmutableKmClass): List<FunSpec> =
    actionClazz.constructors
        .filterNot { it.isPrivate }
        .flatMap { ctr ->
            listOfNotNull(
                generateStandardActionFunctionFor(actionClazz, adapterClazz, ctr),
                actionClazz.takeIf { it.isSubTypeOf(PagedAction::class) }
                    ?.let { generatePagedActionFunctionFor(actionClazz, adapterClazz, ctr) }
            )
        }

@KotlinPoetMetadataPreview
private fun generatePagedActionFunctionFor(
    actionClazz: ImmutableKmClass,
    adapterClazz: ImmutableKmClass,
    ctr: ImmutableKmConstructor
) = generateExtensionFunction(
    actionClazz, adapterClazz, ctr, "Paginated", CodeBlock.of(
        "return org.http4k.connect.paginated(::invoke, %T(${ctr.valueParameters.joinToString(", ") { it.name }}))",
        actionClazz.name.asClassName()
    )
)

@KotlinPoetMetadataPreview
private fun generateStandardActionFunctionFor(
    actionClazz: ImmutableKmClass,
    adapterClazz: ImmutableKmClass,
    ctr: ImmutableKmConstructor
) = generateExtensionFunction(
    actionClazz, adapterClazz, ctr, "", CodeBlock.of(
        "return invoke(%T(${ctr.valueParameters.joinToString(", ") { it.name }}))",
        actionClazz.name.asClassName()
    )
)

@KotlinPoetMetadataPreview
private fun generateExtensionFunction(
    actionClazz: ImmutableKmClass,
    adapterClazz: ImmutableKmClass,
    ctr: ImmutableKmConstructor,
    suffix: String,
    codeBlock: CodeBlock
): FunSpec {
    val baseFunction = FunSpec.builder(actionClazz.name.name().decapitalize() + suffix)
        .addKdoc("@see ${actionClazz.name.replace('/', '.')}")
        .receiver(adapterClazz.name.asClassName())
        .addCode(codeBlock)

    ctr.valueParameters.forEach {
        val base = ParameterSpec.builder(it.name, it.type!!.generifiedType())
        if (it.type!!.isNullable) base.defaultValue(CodeBlock.of("null"))
        baseFunction.addParameter(base.build())
    }
    return baseFunction.build()
}
