package org.http4k.connect.plugin

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier.OVERRIDE
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.metadata.KotlinPoetMetadataPreview
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.Moshi
import org.http4k.connect.Http4kConnectAction
import org.http4k.connect.Http4kConnectMoshiAdapter
import org.http4k.connect.plugin.Http4kConnectProcessor.Companion.KAPT_KOTLIN_GENERATED_OPTION_NAME
import java.io.File
import java.lang.reflect.Type
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

                val actionType = it.name.asClassName()
                val type = TypeSpec.classBuilder(className + "Adapter")
                    .superclass(
                        Http4kConnectMoshiAdapter::class.asClassName().parameterizedBy(actionType))
                    .addFunction(fromJsonFields(actionType))
                    .addFunction(fromObject(actionType))
                    .build()

                fileBuilder.addType(type)

                fileBuilder.build().writeTo(outputDir)
                println(it)
            }
        return true
    }

    private fun fromObject(actionType: ClassName) = FunSpec.builder("fromObject").addModifiers(OVERRIDE)
        .addParameter(ParameterSpec.builder("writer", JsonWriter::class.asClassName()).build())
        .addParameter(ParameterSpec.builder("value", actionType).build())
        .build()

    private fun fromJsonFields(actionType: ClassName) = FunSpec.builder("fromJsonFields")
        .addModifiers(OVERRIDE)
        .addParameter(
            ParameterSpec.builder("fields", Map::class.asClassName().parameterizedBy(
                String::class.asClassName(),
                Any::class.asClassName()
            )).build())
        .returns(actionType)
        .build()
}

class Messag2JsonAdapter1(moshi: Moshi) : Http4kConnectMoshiAdapter<Message>() {
    private val description = moshi.adapter(String::class.java)

    override fun fromJsonFields(fields: Map<String, Any>) =
        Message(
            fields["Description"]?.let(description::fromJsonValue)
        )

    override fun fromObject(writer: JsonWriter, it: Message) {
        writer.name("Description")
        description.toJson(writer, it.Description)
    }
}


object KMSMoshiAdapterFactory : JsonAdapter.Factory {
    override fun create(type: Type, annotations: Set<Annotation>, moshi: Moshi): JsonAdapter<*>? =
        when (type.typeName) {
//            CreateKey2::class.java.typeName -> CreateKey2JsonAdapter1(moshi).nullSafe()
            else -> null
        }
}

data class Message(val Description: String?)
