package org.http4k.connect.plugin

import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.metadata.KotlinPoetMetadataPreview
import com.squareup.kotlinpoet.metadata.toImmutableKmClass
import org.http4k.connect.Http4kConnectAction
import org.http4k.connect.Http4kConnectAdapter
import java.io.File
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.RoundEnvironment
import javax.annotation.processing.SupportedAnnotationTypes
import javax.annotation.processing.SupportedOptions
import javax.annotation.processing.SupportedSourceVersion
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic.Kind.ERROR

@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes("org.http4k.connect.Http4kConnectAdapter")
@SupportedOptions(Http4kConnectAdapterGenerator.KAPT_KOTLIN_GENERATED_OPTION_NAME)
class Http4kConnectAdapterGenerator : AbstractProcessor() {

    @OptIn(KotlinPoetMetadataPreview::class)
    override fun process(annotations: MutableSet<out TypeElement>?, roundEnv: RoundEnvironment): Boolean {
        val annotatedElements = roundEnv.getElementsAnnotatedWith(Http4kConnectAdapter::class.java)
        if (annotatedElements.isEmpty()) return false

        val kaptKotlinGeneratedDir = processingEnv.options[KAPT_KOTLIN_GENERATED_OPTION_NAME] ?: run {
            processingEnv.messager.printMessage(ERROR, "Can't find the target directory for generated Kotlin files.")
            return false
        }

        return roundEnv.annotated<Http4kConnectAdapter>()
            .filterIsInstance<TypeElement>()
            .firstOrNull()
            ?.toImmutableKmClass()
            ?.let { adapter ->
                roundEnv.annotated<Http4kConnectAction>()
                    .filterIsInstance<TypeElement>()
                    .map { it.toImmutableKmClass() }
                    .forEach {
                        println(it)
                    }

                FileSpec.builder(
                    adapter.name.substringBeforeLast("/").replace('/', '.'),
                    adapter.name.substringAfterLast('/').toLowerCase() + "Extensions").build()
                    .writeTo(File(kaptKotlinGeneratedDir))

                true
            }
            ?: false
    }

    companion object {
        const val KAPT_KOTLIN_GENERATED_OPTION_NAME = "kapt.kotlin.generated"
    }

}

private inline fun <reified T : Annotation> RoundEnvironment.annotated() =
    rootElements.filter { it.getAnnotation(T::class.java) != null }

