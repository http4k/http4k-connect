package org.http4k.connect.amazon.firehose

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import org.http4k.connect.amazon.model.DeliveryStreamName
import org.http4k.format.AwsCoreJsonAdapterFactory
import org.http4k.format.CollectionEdgeCasesAdapter
import org.http4k.format.ConfigurableMoshi
import org.http4k.format.asConfigurable
import org.http4k.format.value
import org.http4k.format.withAwsCoreMappings
import org.http4k.format.withStandardMappings
import se.ansman.kotshi.KotshiJsonAdapterFactory

object FirehoseMoshi : ConfigurableMoshi(
    Moshi.Builder()
        .add(KotshiFirehoseJsonAdapterFactory)
        .add(AwsCoreJsonAdapterFactory())
        .add(CollectionEdgeCasesAdapter)
        .asConfigurable()
        .withStandardMappings()
        .withAwsCoreMappings()
        .value(DeliveryStreamName)
        .done()
)

@KotshiJsonAdapterFactory
abstract class FirehoseJsonAdapterFactory : JsonAdapter.Factory
