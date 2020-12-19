package org.http4k.connect.plugin

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.asTypeName
import com.squareup.kotlinpoet.metadata.ImmutableKmClass
import com.squareup.kotlinpoet.metadata.ImmutableKmType
import com.squareup.kotlinpoet.metadata.KotlinPoetMetadataPreview
import com.squareup.kotlinpoet.metadata.isNullable
import com.squareup.kotlinpoet.metadata.isPrivate
import com.squareup.kotlinpoet.metadata.toImmutableKmClass
import dev.forkhandles.result4k.Result
import kotlinx.metadata.KmClassifier.Class
import org.http4k.connect.Http4kConnectAction
import org.http4k.connect.Http4kConnectAdapter
import org.http4k.connect.RemoteFailure
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
@SupportedAnnotationTypes("org.http4k.connect.Http4kConnectAdapter")
@SupportedOptions(KAPT_KOTLIN_GENERATED_OPTION_NAME)
class Http4kConnectAdapterProcessor : Http4kConnectProcessor() {

    override fun generate(annotations: Set<TypeElement>, roundEnv: RoundEnvironment, outputDir: File): Boolean {
        roundEnv.annotated<Http4kConnectAdapter>()
            .filterIsInstance<TypeElement>()
            .forEach { it.toImmutableKmClass().generateActionExtensionsFor(roundEnv, outputDir) }

        return true
    }

    private fun ImmutableKmClass.generateActionExtensionsFor(roundEnv: RoundEnvironment, outputDir: File) {
        val (packageName, className) = explodeName()

        val actionType = functions.find { it.name == "invoke" }!!.valueParameters.first()
        val fileBuilder = FileSpec.builder(packageName, className.toLowerCase() + "Extensions")

        roundEnv.annotated<Http4kConnectAction>()
            .filterIsInstance<TypeElement>()
            .map { it.toImmutableKmClass() }
            .filter {
                it.supertypes.map { it.classifier }.contains(actionType.type?.classifier)
            }
            .flatMap { generateActionFunction(it) }
            .forEach(fileBuilder::addFunction)

        fileBuilder.build().writeTo(outputDir)
    }
}

@KotlinPoetMetadataPreview
private fun ImmutableKmClass.generateActionFunction(it: ImmutableKmClass): List<FunSpec> {
    val message = it.supertypes.first().arguments.first().type!!.generifiedType()
    val (_, actionClazz) = it.explodeName()

    val actionClassName = it.name.asClassName()

    return it.constructors
        .filterNot { it.isPrivate }
        .map {
            val baseFunction = FunSpec.builder(actionClazz.decapitalize())
                .receiver(name.asClassName())
                .addCode(CodeBlock.of("return this(%T(${it.valueParameters.joinToString(", ") { it.name.decapitalize() }}))", actionClassName))
                .returns(Result::class.asTypeName()
                    .parameterizedBy(listOf(message, RemoteFailure::class.asTypeName())))

            it.valueParameters.forEach {
                val base = ParameterSpec.builder(it.name.decapitalize(), it.type!!.generifiedType())
                if (it.type!!.isNullable) base.defaultValue(CodeBlock.of("null"))
                baseFunction.addParameter(base.build())
            }
            baseFunction.build()
        }
}

@KotlinPoetMetadataPreview
private fun ImmutableKmType.generifiedType(): TypeName {
    val base = (classifier as Class).name.asClassName()
    return when {
        arguments.isEmpty() -> base.copy(nullable = isNullable)
        else -> base.parameterizedBy(arguments.map { it.type!!.generifiedType() }).copy(nullable = isNullable)
    }
}

@KotlinPoetMetadataPreview
private fun ImmutableKmClass.explodeName() = name.pkg() to name.substringAfterLast('/')

private fun kotlinx.metadata.ClassName.pkg() = substringBeforeLast("/").replace('/', '.')
private fun kotlinx.metadata.ClassName.name() = substringAfterLast('/')
private fun kotlinx.metadata.ClassName.asClassName() = ClassName(pkg(), name())

