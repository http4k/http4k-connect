package org.http4k.connect.plugin

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.metadata.ImmutableKmClass
import com.squareup.kotlinpoet.metadata.KotlinPoetMetadataPreview
import com.squareup.kotlinpoet.metadata.toImmutableKmClass
import org.http4k.connect.Http4kConnectAction
import org.http4k.connect.Http4kConnectAdapter
import org.http4k.connect.plugin.Http4kConnectAdapterGenerator.Companion.KAPT_KOTLIN_GENERATED_OPTION_NAME
import java.io.File
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.RoundEnvironment
import javax.annotation.processing.SupportedAnnotationTypes
import javax.annotation.processing.SupportedOptions
import javax.annotation.processing.SupportedSourceVersion
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic.Kind.ERROR

@KotlinPoetMetadataPreview
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes("org.http4k.connect.Http4kConnectAdapter")
@SupportedOptions(KAPT_KOTLIN_GENERATED_OPTION_NAME)
class Http4kConnectAdapterGenerator : AbstractProcessor() {

    override fun process(annotations: MutableSet<out TypeElement>, roundEnv: RoundEnvironment): Boolean {
        outputDir() ?: run {
            processingEnv.messager.printMessage(ERROR, "Can't find the target directory for generated Kotlin files.")
            return false
        }

        roundEnv.annotated<Http4kConnectAdapter>()
            .filterIsInstance<TypeElement>()
            .firstOrNull()
            ?.toImmutableKmClass()
            ?.generateActionExtensionsFor(roundEnv)

        return true
    }

    private fun ImmutableKmClass.generateActionExtensionsFor(roundEnv: RoundEnvironment) {
        val (packageName, className) = explodeName()


        val fileBuilder = FileSpec.builder(
            packageName,
            className.toLowerCase() + "Extensions")

        roundEnv.annotated<Http4kConnectAction>()
            .filterIsInstance<TypeElement>()
            .map { it.toImmutableKmClass() }
            .forEach {
                val (actionPkg, actionClazz) = it.explodeName()
                fileBuilder.addFunction(FunSpec.builder(actionClazz.decapitalize())
                    .receiver(ClassName.bestGuess(name.replace('/', '.')))
                    .build())
            }

        fileBuilder.build().writeTo(File(outputDir()!!))
    }

    private fun ImmutableKmClass.explodeName(): Pair<String, String> {
        val packageName = name.substringBeforeLast("/").replace('/', '.')
        val className = name.substringAfterLast('/')
        return packageName to className
    }

    private fun outputDir() = processingEnv.options[KAPT_KOTLIN_GENERATED_OPTION_NAME]

    companion object {
        const val KAPT_KOTLIN_GENERATED_OPTION_NAME = "kapt.kotlin.generated"
    }

}

private inline fun <reified T : Annotation> RoundEnvironment.annotated() =
    rootElements.filter { it.getAnnotation(T::class.java) != null }

