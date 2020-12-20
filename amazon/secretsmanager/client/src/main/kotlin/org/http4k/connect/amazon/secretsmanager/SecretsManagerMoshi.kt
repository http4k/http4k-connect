package org.http4k.connect.amazon.secretsmanager

import com.squareup.moshi.Moshi
import org.http4k.connect.amazon.model.SecretId
import org.http4k.connect.amazon.model.VersionId
import org.http4k.connect.amazon.model.VersionStage
import org.http4k.format.ConfigurableMoshi
import org.http4k.format.asConfigurable
import org.http4k.format.text
import org.http4k.format.withAwsCoreMappings
import org.http4k.format.withStandardMappings

object SecretsManagerMoshi : ConfigurableMoshi(Moshi.Builder()
    .asConfigurable()
    .withStandardMappings()
    .withAwsCoreMappings()
    .text(SecretId::of)
    .text(VersionId::of)
    .text(VersionStage::of)
    .done()
)
