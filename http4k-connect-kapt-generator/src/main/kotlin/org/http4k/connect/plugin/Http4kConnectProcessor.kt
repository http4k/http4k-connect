package org.http4k.connect.plugin

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.metadata.ImmutableKmClass
import com.squareup.kotlinpoet.metadata.KotlinPoetMetadataPreview
import com.squareup.kotlinpoet.metadata.toImmutableKmClass
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
        .map { it.toImmutableKmClass()}


@KotlinPoetMetadataPreview
internal fun ImmutableKmClass.explodeName() = name.pkg() to name.name()

internal fun kotlinx.metadata.ClassName.pkg() = substringBeforeLast("/").replace('/', '.')
internal fun kotlinx.metadata.ClassName.name() = substringAfterLast('/')
internal fun kotlinx.metadata.ClassName.asClassName() = ClassName(pkg(), name())

