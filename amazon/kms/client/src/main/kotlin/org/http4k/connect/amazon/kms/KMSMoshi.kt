package org.http4k.connect.amazon.kms

import com.squareup.moshi.Moshi
import org.http4k.connect.amazon.kms.action.KotshiCreateKeyJsonAdapter
import org.http4k.connect.amazon.kms.action.KotshiDecryptJsonAdapter
import org.http4k.connect.amazon.kms.action.KotshiDecryptedJsonAdapter
import org.http4k.connect.amazon.kms.action.KotshiDescribeKeyJsonAdapter
import org.http4k.connect.amazon.kms.action.KotshiEncryptJsonAdapter
import org.http4k.connect.amazon.kms.action.KotshiEncryptedJsonAdapter
import org.http4k.connect.amazon.kms.action.KotshiGetPublicKeyJsonAdapter
import org.http4k.connect.amazon.kms.action.KotshiKeyCreatedJsonAdapter
import org.http4k.connect.amazon.kms.action.KotshiKeyDeletionScheduleJsonAdapter
import org.http4k.connect.amazon.kms.action.KotshiKeyDescriptionJsonAdapter
import org.http4k.connect.amazon.kms.action.KotshiKeyEntryJsonAdapter
import org.http4k.connect.amazon.kms.action.KotshiKeyListJsonAdapter
import org.http4k.connect.amazon.kms.action.KotshiListKeysJsonAdapter
import org.http4k.connect.amazon.kms.action.KotshiPublicKeyJsonAdapter
import org.http4k.connect.amazon.kms.action.KotshiScheduleKeyDeletionJsonAdapter
import org.http4k.connect.amazon.kms.action.KotshiSignJsonAdapter
import org.http4k.connect.amazon.kms.action.KotshiSignedJsonAdapter
import org.http4k.connect.amazon.kms.action.KotshiVerifyJsonAdapter
import org.http4k.connect.amazon.kms.action.KotshiVerifyResultJsonAdapter
import org.http4k.connect.amazon.model.KotshiKeyMetadataJsonAdapter
import org.http4k.format.AwsJsonAdapterFactory
import org.http4k.format.ConfigurableMoshi
import org.http4k.format.adapter
import org.http4k.format.asConfigurable
import org.http4k.format.withAwsCoreMappings
import org.http4k.format.withStandardMappings

object KMSMoshi : ConfigurableMoshi(
    Moshi.Builder()
        .add(KMSJsonAdapterFactory)
        .asConfigurable()
        .withStandardMappings()
        .withAwsCoreMappings()
        .done()
)

object KMSJsonAdapterFactory : AwsJsonAdapterFactory(
    adapter(::KotshiCreateKeyJsonAdapter),
    adapter(::KotshiDecryptedJsonAdapter),
    adapter(::KotshiDecryptJsonAdapter),
    adapter(::KotshiDescribeKeyJsonAdapter),
    adapter(::KotshiEncryptedJsonAdapter),
    adapter(::KotshiEncryptJsonAdapter),
    adapter(::KotshiGetPublicKeyJsonAdapter),
    adapter(::KotshiKeyCreatedJsonAdapter),
    adapter(::KotshiKeyDeletionScheduleJsonAdapter),
    adapter(::KotshiKeyDescriptionJsonAdapter),
    adapter(::KotshiKeyEntryJsonAdapter),
    adapter(::KotshiKeyMetadataJsonAdapter),
    adapter { KotshiListKeysJsonAdapter() },
    adapter(::KotshiKeyListJsonAdapter),
    adapter(::KotshiPublicKeyJsonAdapter),
    adapter(::KotshiScheduleKeyDeletionJsonAdapter),
    adapter(::KotshiSignedJsonAdapter),
    adapter(::KotshiSignJsonAdapter),
    adapter(::KotshiVerifyJsonAdapter),
    adapter(::KotshiVerifyResultJsonAdapter)
)
