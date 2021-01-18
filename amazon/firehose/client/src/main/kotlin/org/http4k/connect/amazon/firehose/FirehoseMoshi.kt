package org.http4k.connect.amazon.firehose

import com.squareup.moshi.Moshi
import org.http4k.connect.amazon.firehose.action.KotshiBatchResultJsonAdapter
import org.http4k.connect.amazon.firehose.action.KotshiPutRecordBatchJsonAdapter
import org.http4k.connect.amazon.firehose.action.KotshiPutRecordJsonAdapter
import org.http4k.connect.amazon.firehose.action.KotshiRecordAddedJsonAdapter
import org.http4k.connect.amazon.firehose.action.KotshiRecordJsonAdapter
import org.http4k.connect.amazon.firehose.action.KotshiRequestResponsesJsonAdapter
import org.http4k.connect.amazon.model.DeliveryStreamName
import org.http4k.format.AwsJsonAdapterFactory
import org.http4k.format.ConfigurableMoshi
import org.http4k.format.adapter
import org.http4k.format.asConfigurable
import org.http4k.format.withAwsCoreMappings
import org.http4k.format.withStandardMappings
import value

object FirehoseMoshi : ConfigurableMoshi(
    Moshi.Builder()
        .add(FirehoseJsonAdapterFactory)
        .asConfigurable()
        .withStandardMappings()
        .withAwsCoreMappings()
        .value(DeliveryStreamName)
        .done()
)

object FirehoseJsonAdapterFactory : AwsJsonAdapterFactory(
    adapter(::KotshiBatchResultJsonAdapter),
    adapter(::KotshiPutRecordBatchJsonAdapter),
    adapter(::KotshiPutRecordJsonAdapter),
    adapter(::KotshiRecordJsonAdapter),
    adapter { KotshiRecordAddedJsonAdapter() },
    adapter { KotshiRequestResponsesJsonAdapter() }
)
