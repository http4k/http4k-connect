package org.http4k.connect.plugin

import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.metadata.ImmutableKmClass
import com.squareup.kotlinpoet.metadata.ImmutableKmValueParameter
import com.squareup.kotlinpoet.metadata.KotlinPoetMetadataPreview
import com.squareup.kotlinpoet.metadata.isNullable
import com.squareup.kotlinpoet.metadata.isPrivate
import com.squareup.kotlinpoet.metadata.toImmutableKmClass
import kotlinx.metadata.KmClassifier
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
import javax.lang.model.type.TypeMirror

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

        val actionVP: ImmutableKmValueParameter = functions.find { it.name == "invoke" }!!.valueParameters.first()
        val actionType: TypeElement = processingEnv.elementUtils.getTypeElement(
            (actionVP.type!!.classifier as KmClassifier.Class).name.replace('/', '.')
        )

        roundEnv.annotated<Http4kConnectAction>()
            .forEach {
                println(it.asType())
                println("ERASURE" + processingEnv.typeUtils.erasure(actionType.asType()).toString())
                val map = processingEnv.typeUtils.directSupertypes(it.asType())
                    .map { processingEnv.typeUtils.erasure(it).toString() }
                val element = processingEnv.typeUtils.erasure(actionType.asType()).toString()
                println(element)
                println(map)
            }

        if (true) error("")

        val functions = roundEnv.annotated<Http4kConnectAction>()
            .map { it.toImmutableKmClass() }
            .flatMap(::generateActionFunctions)

        return FileSpec.builder(packageName, className.toLowerCase() + "Extensions")
            .apply { functions.forEach(::addFunction) }
            .build()
    }
    fun superTypesOf(it: TypeMirror): List<TypeMirror> =
        processingEnv.typeUtils.directSupertypes(it).flatMap { superTypesOf(it) }
}

@KotlinPoetMetadataPreview
private fun ImmutableKmClass.generateActionFunctions(clazz: ImmutableKmClass) = clazz.constructors
    .filterNot { it.isPrivate }
    .map {
        val baseFunction = FunSpec.builder(clazz.name.name().decapitalize())
            .addKdoc("@see ${clazz.name.replace('/', '.')}")
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
