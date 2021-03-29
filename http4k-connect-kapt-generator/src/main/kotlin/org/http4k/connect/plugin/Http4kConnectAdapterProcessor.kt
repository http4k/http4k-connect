package org.http4k.connect.plugin

import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.metadata.ImmutableKmClass
import com.squareup.kotlinpoet.metadata.KotlinPoetMetadataPreview
import com.squareup.kotlinpoet.metadata.isNullable
import com.squareup.kotlinpoet.metadata.isPrivate
import org.http4k.connect.Http4kConnectAction
import org.http4k.connect.Http4kConnectAdapter
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
            .map { it.generateActionExtensionsFor(roundEnv) }
            .forEach { it.writeTo(outputDir) }
        return true
    }

    private fun ImmutableKmClass.generateActionExtensionsFor(roundEnv: RoundEnvironment): FileSpec {
        val (packageName, className) = explodeName()

        val actionType = functions.find { it.name == "invoke" }!!.valueParameters.first()

        val functions = roundEnv.annotated<Http4kConnectAction>()
            .filter { it.supertypes.map { it.classifier }.contains(actionType.type?.classifier) }
            .flatMap(::generateActionFunctions)

        return FileSpec.builder(packageName, className.toLowerCase() + "Extensions")
            .apply { functions.forEach(::addFunction) }
            .build()
    }
}

@KotlinPoetMetadataPreview
private fun ImmutableKmClass.generateActionFunctions(clazz: ImmutableKmClass) = clazz.constructors
    .filterNot { it.isPrivate }
    .map {
        val baseFunction = FunSpec.builder(clazz.name.name().decapitalize())
            .receiver(name.asClassName())
            .addCode(
                CodeBlock.of(
                    "return this(%T(${it.valueParameters.joinToString(", ") { it.name }}))",
                    clazz.name.asClassName()
                )
            )

        it.valueParameters.forEach {
            val base = ParameterSpec.builder(it.name, it.type!!.generifiedType())
            if (it.type!!.isNullable) base.defaultValue(CodeBlock.of("null"))
            baseFunction.addParameter(base.build())
        }
        baseFunction.build()
    }
