package org.http4k.connect.plugin

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.metadata.ImmutableKmClass
import com.squareup.kotlinpoet.metadata.ImmutableKmType
import com.squareup.kotlinpoet.metadata.KotlinPoetMetadataPreview
import com.squareup.kotlinpoet.metadata.isNullable
import com.squareup.kotlinpoet.metadata.toImmutableKmClass
import kotlinx.metadata.KmClassifier
import java.io.File
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic.Kind.ERROR

@KotlinPoetMetadataPreview
abstract class Http4kConnectProcessor : AbstractProcessor() {

    abstract fun generate(annotations: Set<TypeElement>, roundEnv: RoundEnvironment, outputDir: File): Boolean

    override fun process(annotations: Set<TypeElement>, roundEnv: RoundEnvironment) = outputDir()
        ?.let { generate(annotations, roundEnv, File(it)) }
        ?: run {
            processingEnv.messager.printMessage(ERROR, "Can't find the target directory for generated Kotlin files.")
            false
        }

    private fun outputDir() = processingEnv.options[KAPT_KOTLIN_GENERATED_OPTION_NAME]

    companion object {
        const val KAPT_KOTLIN_GENERATED_OPTION_NAME = "kapt.kotlin.generated"
    }
}

@KotlinPoetMetadataPreview
internal inline fun <reified T : Annotation> RoundEnvironment.annotated() =
    rootElements.filter { it.getAnnotation(T::class.java) != null }
        .filterIsInstance<TypeElement>()
        .map { it.toImmutableKmClass() }


@KotlinPoetMetadataPreview
internal fun ImmutableKmClass.explodeName() = name.pkg() to name.name()

@KotlinPoetMetadataPreview
internal fun ImmutableKmClass.poetClassName() = ClassName(name.pkg(), name.name())

@KotlinPoetMetadataPreview
internal fun ImmutableKmType.poetClassName() = ClassName(toString(), toString())

internal fun kotlinx.metadata.ClassName.pkg() = substringBeforeLast("/").replace('/', '.')
internal fun kotlinx.metadata.ClassName.name() = substringAfterLast('/')
internal fun kotlinx.metadata.ClassName.asClassName() = ClassName(pkg(), name())

internal inline fun <reified T> className() = T::class.asClassName()

@KotlinPoetMetadataPreview
internal fun ImmutableKmType.generifiedType(): TypeName {
    val base = (classifier as KmClassifier.Class).name.asClassName()
    return when {
        arguments.isEmpty() -> base.copy(nullable = isNullable)
        else -> base.parameterizedBy(arguments.map { it.type!!.generifiedType() }).copy(nullable = isNullable)
    }
}
