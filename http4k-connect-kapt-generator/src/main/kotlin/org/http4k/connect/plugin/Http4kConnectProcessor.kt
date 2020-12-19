package org.http4k.connect.plugin

import com.squareup.kotlinpoet.metadata.KotlinPoetMetadataPreview
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
