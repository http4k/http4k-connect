package org.http4k.connect.amazon.firehose.action

import org.http4k.connect.Http4kConnectAction
import org.http4k.connect.amazon.DeliveryStreamType
import org.http4k.connect.amazon.core.model.ARN
import org.http4k.connect.amazon.core.model.Tag
import org.http4k.connect.amazon.model.DeliveryStreamEncryptionConfigurationInput
import org.http4k.connect.amazon.model.DeliveryStreamName
import org.http4k.connect.amazon.model.ElasticsearchDestinationConfiguration
import org.http4k.connect.amazon.model.ExtendedS3DestinationConfiguration
import org.http4k.connect.amazon.model.HttpEndpointDestinationConfiguration
import org.http4k.connect.amazon.model.KinesisStreamSourceConfiguration
import org.http4k.connect.amazon.model.RedshiftDestinationConfiguration
import org.http4k.connect.amazon.model.S3DestinationConfiguration
import org.http4k.connect.amazon.model.SplunkDestinationConfiguration
import se.ansman.kotshi.JsonSerializable

@Http4kConnectAction
@JsonSerializable
data class CreateDeliveryStream internal constructor(
    val DeliveryStreamName: DeliveryStreamName,
    val DeliveryStreamType: DeliveryStreamType,
    val DeliveryStreamEncryptionConfigurationInput: DeliveryStreamEncryptionConfigurationInput? = null,
    val ElasticsearchDestinationConfiguration: ElasticsearchDestinationConfiguration? = null,
    val ExtendedS3DestinationConfiguration: ExtendedS3DestinationConfiguration? = null,
    val HttpEndpointDestinationConfiguration: HttpEndpointDestinationConfiguration? = null,
    val KinesisStreamSourceConfiguration: KinesisStreamSourceConfiguration? = null,
    val RedshiftDestinationConfiguration: RedshiftDestinationConfiguration? = null,
    val S3DestinationConfiguration: S3DestinationConfiguration? = null,
    val SplunkDestinationConfiguration: SplunkDestinationConfiguration? = null,
    val Tags: List<Tag>? = null
) : FirehoseAction<CreatedDeliveryStream>(CreatedDeliveryStream::class) {

    companion object {
        fun ElasticSearch(
            ElasticsearchDestinationConfiguration: ElasticsearchDestinationConfiguration,
            DeliveryStreamName: DeliveryStreamName,
            DeliveryStreamType: DeliveryStreamType,
            DeliveryStreamEncryptionConfigurationInput: DeliveryStreamEncryptionConfigurationInput? = null,
            Tags: List<Tag>? = null
        ) = CreateDeliveryStream(
            DeliveryStreamName,
            DeliveryStreamType,
            ElasticsearchDestinationConfiguration = ElasticsearchDestinationConfiguration,
            DeliveryStreamEncryptionConfigurationInput = DeliveryStreamEncryptionConfigurationInput,
            Tags = Tags
        )

        fun ExtendedS3(
            ExtendedS3DestinationConfiguration: ExtendedS3DestinationConfiguration,
            DeliveryStreamName: DeliveryStreamName,
            DeliveryStreamType: DeliveryStreamType,
            DeliveryStreamEncryptionConfigurationInput: DeliveryStreamEncryptionConfigurationInput? = null,
            Tags: List<Tag>? = null
        ) = CreateDeliveryStream(
            DeliveryStreamName,
            DeliveryStreamType,
            ExtendedS3DestinationConfiguration = ExtendedS3DestinationConfiguration,
            DeliveryStreamEncryptionConfigurationInput = DeliveryStreamEncryptionConfigurationInput,
            Tags = Tags
        )

        fun Http(
            HttpEndpointDestinationConfiguration: HttpEndpointDestinationConfiguration,
            DeliveryStreamName: DeliveryStreamName,
            DeliveryStreamType: DeliveryStreamType,
            DeliveryStreamEncryptionConfigurationInput: DeliveryStreamEncryptionConfigurationInput? = null,
            Tags: List<Tag>? = null
        ) = CreateDeliveryStream(
            DeliveryStreamName,
            DeliveryStreamType,
            HttpEndpointDestinationConfiguration = HttpEndpointDestinationConfiguration,
            DeliveryStreamEncryptionConfigurationInput = DeliveryStreamEncryptionConfigurationInput,
            Tags = Tags
        )

        fun Kinesis(
            KinesisStreamSourceConfiguration: KinesisStreamSourceConfiguration,
            DeliveryStreamName: DeliveryStreamName,
            DeliveryStreamType: DeliveryStreamType,
            DeliveryStreamEncryptionConfigurationInput: DeliveryStreamEncryptionConfigurationInput? = null,
            Tags: List<Tag>? = null
        ) = CreateDeliveryStream(
            DeliveryStreamName,
            DeliveryStreamType,
            KinesisStreamSourceConfiguration = KinesisStreamSourceConfiguration,
            DeliveryStreamEncryptionConfigurationInput = DeliveryStreamEncryptionConfigurationInput,
            Tags = Tags
        )

        fun Redshift(
            RedshiftDestinationConfiguration: RedshiftDestinationConfiguration,
            DeliveryStreamName: DeliveryStreamName,
            DeliveryStreamType: DeliveryStreamType,
            DeliveryStreamEncryptionConfigurationInput: DeliveryStreamEncryptionConfigurationInput? = null,
            Tags: List<Tag>? = null
        ) = CreateDeliveryStream(
            DeliveryStreamName,
            DeliveryStreamType,
            RedshiftDestinationConfiguration = RedshiftDestinationConfiguration,
            DeliveryStreamEncryptionConfigurationInput = DeliveryStreamEncryptionConfigurationInput,
            Tags = Tags
        )

        fun S3(
            S3DestinationConfiguration: S3DestinationConfiguration,
            DeliveryStreamName: DeliveryStreamName,
            DeliveryStreamType: DeliveryStreamType,
            DeliveryStreamEncryptionConfigurationInput: DeliveryStreamEncryptionConfigurationInput? = null,
            Tags: List<Tag>? = null
        ) = CreateDeliveryStream(
            DeliveryStreamName,
            DeliveryStreamType,
            S3DestinationConfiguration = S3DestinationConfiguration,
            DeliveryStreamEncryptionConfigurationInput = DeliveryStreamEncryptionConfigurationInput,
            Tags = Tags
        )

        fun Splunk(
            SplunkDestinationConfiguration: SplunkDestinationConfiguration,
            DeliveryStreamName: DeliveryStreamName,
            DeliveryStreamType: DeliveryStreamType,
            DeliveryStreamEncryptionConfigurationInput: DeliveryStreamEncryptionConfigurationInput? = null,
            Tags: List<Tag>? = null
        ) = CreateDeliveryStream(
            DeliveryStreamName,
            DeliveryStreamType,
            SplunkDestinationConfiguration = SplunkDestinationConfiguration,
            DeliveryStreamEncryptionConfigurationInput = DeliveryStreamEncryptionConfigurationInput,
            Tags = Tags
        )
    }
}

@JsonSerializable
data class CreatedDeliveryStream(val DeliveryStreamARN: ARN)
