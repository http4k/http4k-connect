package org.http4k.connect.plugin

import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier.OVERRIDE
import com.squareup.kotlinpoet.KModifier.PRIVATE
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.metadata.ImmutableKmClass
import com.squareup.kotlinpoet.metadata.KotlinPoetMetadataPreview
import com.squareup.kotlinpoet.metadata.isNullable
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
        roundEnv.annotated<Http4kConnectAction>()
            .forEach {
                val (packageName, className) = it.explodeName()

                val fileBuilder = FileSpec.builder(packageName, className + "Adapter")

                val type = TypeSpec.classBuilder(className + "Adapter")
                    .superclass(
                        className<Http4kConnectMoshiAdapter<*>>()
                            .parameterizedBy(it.poetClassName()))
                    .primaryConstructor(FunSpec.constructorBuilder()
                        .addParameter(ParameterSpec("moshi", className<Moshi>()))
                        .build())
                    .addProperties(it.constructors[0].valueParameters.map {
                        val fieldType = it.type!!.generifiedType().copy(nullable = false)
                        PropertySpec.builder(it.name,
                            className<JsonAdapter<*>>()
                                .parameterizedBy(fieldType), PRIVATE)
                            .initializer(CodeBlock.of("moshi.adapter($fieldType::class.java)"))
                            .build()
                    })
                    .addFunction(fromJsonFields(it))
                    .addFunction(fromObject(it))
                    .build()

                fileBuilder.addType(type)
                fileBuilder.build().writeTo(outputDir)
            }
        return true
    }

    private fun fromObject(actionType: ImmutableKmClass) = FunSpec.builder("fromObject").addModifiers(OVERRIDE)
        .addParameter(ParameterSpec.builder("writer", className<JsonWriter>()).build())
        .addParameter(ParameterSpec.builder("value", actionType.name.asClassName()).build())
        .build()

    private fun fromJsonFields(actionType: ImmutableKmClass) = FunSpec.builder("fromJsonFields")
        .addModifiers(OVERRIDE)
        .addParameter(
            ParameterSpec.builder("fields",
                className<Map<*, *>>()
                    .parameterizedBy(className<String>(), className<Any>())
            ).build())
        .addCode(buildFromJsonFieldsBody(actionType))
        .returns(actionType.name.asClassName())
        .build()


    private fun buildFromJsonFieldsBody(actionType: ImmutableKmClass): CodeBlock {
        val content = actionType.constructors.first().valueParameters.map {
            val operator = if(it.type!!.isNullable) "?" else "!!"
            "fields[\"${it.name}\"]${operator}.let(${it.name}::fromJsonValue)" +
             if(it.type!!.isNullable) "" else "!!"
        }.joinToString(",\n")
        return CodeBlock.of("return %T($content)", actionType.name.asClassName())
    }
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
