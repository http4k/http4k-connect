package org.http4k.connect.amazon.secretsmanager

import com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES
import com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES
import com.fasterxml.jackson.databind.DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS
import com.fasterxml.jackson.databind.DeserializationFeature.USE_BIG_INTEGER_FOR_INTS
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.http4k.connect.amazon.ARN
import org.http4k.connect.amazon.Base64Blob
import org.http4k.connect.amazon.Timestamp
import org.http4k.format.ConfigurableJackson
import org.http4k.format.asConfigurable
import org.http4k.format.withStandardMappings
import org.http4k.lens.BiDiMapping

data class SecretId(val value: String)

data class VersionId(val value: String)

data class VersionStage(val value: String)

object GetSecretValue {
    data class Request(
        val SecretId: SecretId,
        val VersionId: VersionId,
        val VersionStage: VersionStage
    )

    data class Response(
        val ARN: ARN,
        val CreatedDate: Timestamp,
        val Name: String,
        val SecretBinary: Base64Blob,
        val SecretString: String,
        val VersionId: VersionId,
        val VersionStages: List<VersionStage>
    )
}

object PutSecretValue {
    data class Request(
        val ClientRequestToken: String,
        val SecretBinary: Base64Blob,
        val SecretId: SecretId,
        val SecretString: String,
        val VersionStages: List<VersionStage>
    )

    data class Response(
        val ARN: ARN,
        val Name: String,
        val VersionId: VersionId,
        val VersionStages: List<VersionStage>
    )
}

internal object SecretsManagerJackson : ConfigurableJackson(KotlinModule()
    .asConfigurable()
    .withStandardMappings()
    .text(BiDiMapping(::ARN, ARN::value))
    .text(BiDiMapping(::SecretId, SecretId::value))
    .text(BiDiMapping(::Base64Blob, Base64Blob::base64Encoded))
    .long(BiDiMapping(::Timestamp, Timestamp::value))
    .text(BiDiMapping(::VersionId, VersionId::value))
    .text(BiDiMapping(::VersionStage, VersionStage::value))
    .done()
    .deactivateDefaultTyping()
    .configure(FAIL_ON_UNKNOWN_PROPERTIES, false)
    .configure(FAIL_ON_IGNORED_PROPERTIES, false)
    .configure(USE_BIG_DECIMAL_FOR_FLOATS, true)
    .configure(USE_BIG_INTEGER_FOR_INTS, true)
)

