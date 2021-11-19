package org.http4k.connect.plugin

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.metadata.KotlinPoetMetadataPreview
import com.squareup.kotlinpoet.metadata.isNullable
import kotlinx.metadata.KmClass
import kotlinx.metadata.KmClassifier
import kotlinx.metadata.KmType
import java.io.File
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.TypeElement
import javax.lang.model.type.TypeMirror
import javax.tools.Diagnostic.Kind.ERROR
import kotlin.reflect.KClass

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

    protected fun rawType(typeMirror: TypeMirror) =
        processingEnv.typeUtils.erasure(typeMirror).toString()

    protected fun superTypesOf(type: TypeMirror): List<TypeMirror> =
        processingEnv.typeUtils.directSupertypes(type).flatMap { superTypesOf(it) + it }

    companion object {
        const val KAPT_KOTLIN_GENERATED_OPTION_NAME = "kapt.kotlin.generated"
    }
}

@KotlinPoetMetadataPreview
internal inline fun <reified T : Annotation> RoundEnvironment.annotated(): List<TypeElement> = rootElements
    .filter { it.getAnnotation(T::class.java) != null }
    .filterIsInstance<TypeElement>()

@KotlinPoetMetadataPreview
internal fun KmClass.explodeName() = name.pkg() to name.name()
internal fun kotlinx.metadata.ClassName.pkg() = substringBeforeLast("/").replace('/', '.')
internal fun kotlinx.metadata.ClassName.name() = substringAfterLast('/')
internal fun kotlinx.metadata.ClassName.asClassName() = ClassName(pkg(), name())

@KotlinPoetMetadataPreview
internal fun KmType.generifiedType(): TypeName {
    val base = (classifier as KmClassifier.Class).name.asClassName()
    return when {
        arguments.isEmpty() -> base.copy(nullable = isNullable)
        else -> base.parameterizedBy(arguments.map { it.type!!.generifiedType() }).copy(nullable = isNullable)
    }
}

@KotlinPoetMetadataPreview
fun KmClass.isSubTypeOf(kClass: KClass<*>) =
    supertypes.map { it.classifier }.filterIsInstance<KmClassifier.Class>().map { it.name.asClassName() }
        .contains(kClass.asClassName())
