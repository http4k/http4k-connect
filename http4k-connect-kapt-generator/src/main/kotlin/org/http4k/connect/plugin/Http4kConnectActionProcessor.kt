package org.http4k.connect.plugin

import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.metadata.KotlinPoetMetadataPreview
import org.http4k.connect.Http4kConnectAction
import org.http4k.connect.plugin.Http4kConnectProcessor.Companion.KAPT_KOTLIN_GENERATED_OPTION_NAME
import java.io.File
import javax.annotation.processing.RoundEnvironment
import javax.annotation.processing.SupportedAnnotationTypes
import javax.annotation.processing.SupportedOptions
import javax.annotation.processing.SupportedSourceVersion
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement

@KotlinPoetMetadataPreview
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes("org.http4k.connect.Http4kConnectAction")
@SupportedOptions(KAPT_KOTLIN_GENERATED_OPTION_NAME)
class Http4kConnectActionProcessor : Http4kConnectProcessor() {

    override fun generate(annotations: Set<TypeElement>,
                          roundEnv: RoundEnvironment,
                          outputDir: File): Boolean {
        println("HELLO!" + annotations)

        roundEnv.annotated<Http4kConnectAction>()
            .forEach {
                val (packageName, className) = it.explodeName()

                val fileBuilder = FileSpec.builder(packageName,
                    className.toLowerCase() + "Adapter")

                fileBuilder.build().writeTo(outputDir)
                println(it)
            }
        return true
    }
}
