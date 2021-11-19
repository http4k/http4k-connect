package org.http4k.connect.amazon.kms

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import org.http4k.format.AwsCoreJsonAdapterFactory
import org.http4k.format.CollectionEdgeCasesAdapter
import org.http4k.format.ConfigurableMoshi
import org.http4k.format.asConfigurable
import org.http4k.format.withAwsCoreMappings
import org.http4k.format.withStandardMappings
import se.ansman.kotshi.KotshiJsonAdapterFactory

object KMSMoshi : ConfigurableMoshi(
    Moshi.Builder()
        .add(KotshiKMSJsonAdapterFactory)
        .add(AwsCoreJsonAdapterFactory())
        .add(CollectionEdgeCasesAdapter)
        .asConfigurable()
        .withStandardMappings()
        .withAwsCoreMappings()
        .done()
)

@KotshiJsonAdapterFactory
interface KMSJsonAdapterFactory : JsonAdapter.Factory
