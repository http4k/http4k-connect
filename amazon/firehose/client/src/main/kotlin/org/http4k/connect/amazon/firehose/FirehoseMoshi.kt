package org.http4k.connect.amazon.firehose

import com.squareup.moshi.Moshi
import org.http4k.connect.amazon.firehose.action.KotshiBatchResultJsonAdapter
import org.http4k.connect.amazon.firehose.action.KotshiBufferingHintsJsonAdapter
import org.http4k.connect.amazon.firehose.action.KotshiCloudWatchLoggingOptionsJsonAdapter
import org.http4k.connect.amazon.firehose.action.KotshiColumnToJsonKeyMappingsJsonAdapter
import org.http4k.connect.amazon.firehose.action.KotshiCommonAttributesJsonAdapter
import org.http4k.connect.amazon.firehose.action.KotshiCopyCommandJsonAdapter
import org.http4k.connect.amazon.firehose.action.KotshiCreateDeliveryStreamJsonAdapter
import org.http4k.connect.amazon.firehose.action.KotshiCreatedDeliveryStreamJsonAdapter
import org.http4k.connect.amazon.firehose.action.KotshiDataFormatConversionConfigurationJsonAdapter
import org.http4k.connect.amazon.firehose.action.KotshiDeleteDeliveryStreamJsonAdapter
import org.http4k.connect.amazon.firehose.action.KotshiDeliveryStreamEncryptionConfigurationInputJsonAdapter
import org.http4k.connect.amazon.firehose.action.KotshiDeliveryStreamsJsonAdapter
import org.http4k.connect.amazon.firehose.action.KotshiDeserializerJsonAdapter
import org.http4k.connect.amazon.firehose.action.KotshiElasticsearchDestinationConfigurationJsonAdapter
import org.http4k.connect.amazon.firehose.action.KotshiEncryptionConfigurationJsonAdapter
import org.http4k.connect.amazon.firehose.action.KotshiEndpointConfigurationJsonAdapter
import org.http4k.connect.amazon.firehose.action.KotshiExtendedS3DestinationConfigurationJsonAdapter
import org.http4k.connect.amazon.firehose.action.KotshiHiveJsonSerDeJsonAdapter
import org.http4k.connect.amazon.firehose.action.KotshiHttpEndpointDestinationConfigurationJsonAdapter
import org.http4k.connect.amazon.firehose.action.KotshiInputFormatConfigurationJsonAdapter
import org.http4k.connect.amazon.firehose.action.KotshiKMSEncryptionConfigJsonAdapter
import org.http4k.connect.amazon.firehose.action.KotshiKinesisStreamSourceConfigurationJsonAdapter
import org.http4k.connect.amazon.firehose.action.KotshiListDeliveryStreamsJsonAdapter
import org.http4k.connect.amazon.firehose.action.KotshiOpenXJsonSerDeJsonAdapter
import org.http4k.connect.amazon.firehose.action.KotshiOrcSerDeJsonAdapter
import org.http4k.connect.amazon.firehose.action.KotshiOutputFormatConfigurationJsonAdapter
import org.http4k.connect.amazon.firehose.action.KotshiParametersJsonAdapter
import org.http4k.connect.amazon.firehose.action.KotshiParquetSerDeJsonAdapter
import org.http4k.connect.amazon.firehose.action.KotshiProcessingConfigurationJsonAdapter
import org.http4k.connect.amazon.firehose.action.KotshiProcessorJsonAdapter
import org.http4k.connect.amazon.firehose.action.KotshiPutRecordBatchJsonAdapter
import org.http4k.connect.amazon.firehose.action.KotshiPutRecordJsonAdapter
import org.http4k.connect.amazon.firehose.action.KotshiRecordAddedJsonAdapter
import org.http4k.connect.amazon.firehose.action.KotshiRecordJsonAdapter
import org.http4k.connect.amazon.firehose.action.KotshiRedshiftDestinationConfigurationJsonAdapter
import org.http4k.connect.amazon.firehose.action.KotshiRequestConfigurationJsonAdapter
import org.http4k.connect.amazon.firehose.action.KotshiRequestResponsesJsonAdapter
import org.http4k.connect.amazon.firehose.action.KotshiRetryOptionsJsonAdapter
import org.http4k.connect.amazon.firehose.action.KotshiS3BackupConfigurationJsonAdapter
import org.http4k.connect.amazon.firehose.action.KotshiS3ConfigurationJsonAdapter
import org.http4k.connect.amazon.firehose.action.KotshiS3DestinationConfigurationJsonAdapter
import org.http4k.connect.amazon.firehose.action.KotshiSchemaConfigurationJsonAdapter
import org.http4k.connect.amazon.firehose.action.KotshiSerializerJsonAdapter
import org.http4k.connect.amazon.firehose.action.KotshiSplunkDestinationConfigurationJsonAdapter
import org.http4k.connect.amazon.firehose.action.KotshiVpcConfigurationJsonAdapter
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
    adapter { KotshiBufferingHintsJsonAdapter() },
    adapter { KotshiCloudWatchLoggingOptionsJsonAdapter() },
    adapter { KotshiColumnToJsonKeyMappingsJsonAdapter() },
    adapter { KotshiCommonAttributesJsonAdapter() },
    adapter { KotshiCopyCommandJsonAdapter() },
    adapter(::KotshiCreatedDeliveryStreamJsonAdapter),
    adapter(::KotshiCreateDeliveryStreamJsonAdapter),
    adapter(::KotshiDataFormatConversionConfigurationJsonAdapter),
    adapter(::KotshiDeleteDeliveryStreamJsonAdapter),
    adapter(::KotshiDeliveryStreamEncryptionConfigurationInputJsonAdapter),
    adapter(::KotshiDeliveryStreamsJsonAdapter),
    adapter(::KotshiDeserializerJsonAdapter),
    adapter(::KotshiElasticsearchDestinationConfigurationJsonAdapter),
    adapter(::KotshiEncryptionConfigurationJsonAdapter),
    adapter { KotshiEndpointConfigurationJsonAdapter() },
    adapter(::KotshiExtendedS3DestinationConfigurationJsonAdapter),
    adapter(::KotshiHiveJsonSerDeJsonAdapter),
    adapter(::KotshiHttpEndpointDestinationConfigurationJsonAdapter),
    adapter(::KotshiInputFormatConfigurationJsonAdapter),
    adapter(::KotshiKinesisStreamSourceConfigurationJsonAdapter),
    adapter { KotshiKMSEncryptionConfigJsonAdapter() },
    adapter(::KotshiListDeliveryStreamsJsonAdapter),
    adapter(::KotshiOpenXJsonSerDeJsonAdapter),
    adapter(::KotshiOrcSerDeJsonAdapter),
    adapter(::KotshiOutputFormatConfigurationJsonAdapter),
    adapter { KotshiParametersJsonAdapter() },
    adapter { KotshiParquetSerDeJsonAdapter() },
    adapter(::KotshiProcessingConfigurationJsonAdapter),
    adapter(::KotshiProcessorJsonAdapter),
    adapter(::KotshiPutRecordBatchJsonAdapter),
    adapter(::KotshiPutRecordJsonAdapter),
    adapter { KotshiRecordAddedJsonAdapter() },
    adapter(::KotshiRecordJsonAdapter),
    adapter(::KotshiRedshiftDestinationConfigurationJsonAdapter),
    adapter(::KotshiRequestConfigurationJsonAdapter),
    adapter { KotshiRequestResponsesJsonAdapter() },
    adapter { KotshiRetryOptionsJsonAdapter() },
    adapter(::KotshiS3BackupConfigurationJsonAdapter),
    adapter(::KotshiS3ConfigurationJsonAdapter),
    adapter(::KotshiS3DestinationConfigurationJsonAdapter),
    adapter(::KotshiSchemaConfigurationJsonAdapter),
    adapter(::KotshiSerializerJsonAdapter),
    adapter(::KotshiSplunkDestinationConfigurationJsonAdapter),
    adapter(::KotshiVpcConfigurationJsonAdapter)
)
