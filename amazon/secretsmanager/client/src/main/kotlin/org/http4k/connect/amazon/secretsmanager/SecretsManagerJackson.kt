package org.http4k.connect.amazon.secretsmanager

import com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL
import com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES
import com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES
import com.fasterxml.jackson.databind.DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS
import com.fasterxml.jackson.databind.DeserializationFeature.USE_BIG_INTEGER_FOR_INTS
import com.fasterxml.jackson.databind.PropertyNamingStrategy.UPPER_CAMEL_CASE
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.http4k.connect.amazon.model.SecretId
import org.http4k.connect.amazon.model.VersionId
import org.http4k.connect.amazon.model.VersionStage
import org.http4k.format.ConfigurableJackson
import org.http4k.format.asConfigurable
import org.http4k.format.text
import org.http4k.format.withAwsCoreMappings
import org.http4k.format.withStandardMappings

object SecretsManagerJackson : ConfigurableJackson(KotlinModule()
    .asConfigurable()
    .withStandardMappings()
    .withAwsCoreMappings()
    .text(::SecretId)
    .text(::VersionId)
    .text(::VersionStage)
    .done()
    .deactivateDefaultTyping()
    .setPropertyNamingStrategy(UPPER_CAMEL_CASE)
    .setSerializationInclusion(NON_NULL)
    .configure(FAIL_ON_UNKNOWN_PROPERTIES, false)
    .configure(FAIL_ON_IGNORED_PROPERTIES, false)
    .configure(USE_BIG_DECIMAL_FOR_FLOATS, true)
    .configure(USE_BIG_INTEGER_FOR_INTS, true)
)
