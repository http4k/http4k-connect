package org.http4k.connect.amazon.secretsmanager

import com.squareup.moshi.Moshi
import org.http4k.connect.adapter
import org.http4k.connect.amazon.model.SecretId
import org.http4k.connect.amazon.model.VersionId
import org.http4k.connect.amazon.model.VersionStage
import org.http4k.connect.amazon.secretsmanager.action.KotshiCreateSecretJsonAdapter
import org.http4k.connect.amazon.secretsmanager.action.KotshiCreatedSecretJsonAdapter
import org.http4k.connect.amazon.secretsmanager.action.KotshiDeleteSecretJsonAdapter
import org.http4k.connect.amazon.secretsmanager.action.KotshiDeletedSecretJsonAdapter
import org.http4k.connect.amazon.secretsmanager.action.KotshiFiltersJsonAdapter
import org.http4k.connect.amazon.secretsmanager.action.KotshiGetSecretValueJsonAdapter
import org.http4k.connect.amazon.secretsmanager.action.KotshiListSecretsJsonAdapter
import org.http4k.connect.amazon.secretsmanager.action.KotshiPutSecretValueJsonAdapter
import org.http4k.connect.amazon.secretsmanager.action.KotshiRotationRulesJsonAdapter
import org.http4k.connect.amazon.secretsmanager.action.KotshiSecretJsonAdapter
import org.http4k.connect.amazon.secretsmanager.action.KotshiSecretValueJsonAdapter
import org.http4k.connect.amazon.secretsmanager.action.KotshiSecretsJsonAdapter
import org.http4k.connect.amazon.secretsmanager.action.KotshiUpdateSecretJsonAdapter
import org.http4k.connect.amazon.secretsmanager.action.KotshiUpdatedSecretJsonAdapter
import org.http4k.connect.amazon.secretsmanager.action.KotshiUpdatedSecretValueJsonAdapter
import org.http4k.format.AwsActionAdapterFactory
import org.http4k.format.ConfigurableMoshi
import org.http4k.format.asConfigurable
import org.http4k.format.text
import org.http4k.format.withAwsCoreMappings
import org.http4k.format.withStandardMappings

object SecretsManagerMoshi : ConfigurableMoshi(Moshi.Builder()
    .add(SecretsManagerAdapterFactory)
    .asConfigurable()
    .withStandardMappings()
    .withAwsCoreMappings()
    .text(SecretId::of)
    .text(VersionId::of)
    .text(VersionStage::of)
    .done()
)

object SecretsManagerAdapterFactory : AwsActionAdapterFactory(
    adapter(::KotshiCreatedSecretJsonAdapter),
    adapter(::KotshiCreateSecretJsonAdapter),
    adapter(::KotshiDeletedSecretJsonAdapter),
    adapter(::KotshiDeleteSecretJsonAdapter),
    adapter(::KotshiFiltersJsonAdapter),
    adapter(::KotshiGetSecretValueJsonAdapter),
    adapter(::KotshiListSecretsJsonAdapter),
    adapter(::KotshiPutSecretValueJsonAdapter),
    adapter { KotshiRotationRulesJsonAdapter() },
    adapter(::KotshiSecretJsonAdapter),
    adapter(::KotshiSecretsJsonAdapter),
    adapter(::KotshiSecretValueJsonAdapter),
    adapter(::KotshiUpdatedSecretJsonAdapter),
    adapter(::KotshiUpdatedSecretValueJsonAdapter),
    adapter(::KotshiUpdateSecretJsonAdapter),
)
