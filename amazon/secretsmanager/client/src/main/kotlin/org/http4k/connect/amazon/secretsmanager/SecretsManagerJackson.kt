package org.http4k.connect.amazon.secretsmanager

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.module.kotlin.KotlinModule
import dev.forkhandles.values.Value
import org.http4k.connect.amazon.model.ARN
import org.http4k.connect.amazon.model.Base64Blob
import org.http4k.connect.amazon.model.SecretId
import org.http4k.connect.amazon.model.Timestamp
import org.http4k.connect.amazon.model.VersionId
import org.http4k.connect.amazon.model.VersionStage
import org.http4k.format.AutoMappingConfiguration
import org.http4k.format.ConfigurableJackson
import org.http4k.format.asConfigurable
import org.http4k.format.withStandardMappings
import org.http4k.lens.BiDiMapping

internal object SecretsManagerJackson : ConfigurableJackson(KotlinModule()
    .asConfigurable()
    .withStandardMappings()
    .text(::ARN)
    .text(::SecretId)
    .text(::Base64Blob)
    .long(::Timestamp)
    .text(::VersionId)
    .text(::VersionStage)
    .done()
    .deactivateDefaultTyping()
    .setPropertyNamingStrategy(PropertyNamingStrategy.UPPER_CAMEL_CASE)
    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    .configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false)
    .configure(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS, true)
    .configure(DeserializationFeature.USE_BIG_INTEGER_FOR_INTS, true)
)

private inline fun <reified T : Value<String>> AutoMappingConfiguration<ObjectMapper>.text(noinline fn: (String) -> T) = text(BiDiMapping(fn) { it.value })

private inline fun <reified T : Value<Long>> AutoMappingConfiguration<ObjectMapper>.long(noinline fn: (Long) -> T) = long(BiDiMapping(fn) { it.value })
