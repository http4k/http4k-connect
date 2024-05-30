package org.http4k.connect.amazon.autogen

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.http4k.format.ConfigurableJackson
import org.http4k.format.asConfigurable
import org.http4k.format.value
import org.http4k.format.withStandardMappings

object ApiJson : ConfigurableJackson(
    KotlinModule.Builder().build()
        .asConfigurable()
        .withStandardMappings()
        .value(Documentation)
        .value(ShapeName)
        .value(OperationName)
        .value(MemberName)
        .done()
        .deactivateDefaultTyping()
        .setSerializationInclusion(JsonInclude.Include.NON_NULL)
        .configure(JsonParser.Feature.INCLUDE_SOURCE_IN_LOCATION, true)
        .configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, true)
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        .configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false)
        .configure(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS, true)
        .configure(DeserializationFeature.USE_BIG_INTEGER_FOR_INTS, true)
)
