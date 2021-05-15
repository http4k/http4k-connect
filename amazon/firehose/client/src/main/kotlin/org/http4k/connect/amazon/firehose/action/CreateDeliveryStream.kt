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
data class CreateDeliveryStream(
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
) : FirehoseAction<CreatedDeliveryStream>(CreatedDeliveryStream::class)

@JsonSerializable
data class CreatedDeliveryStream(val DeliveryStreamARN: ARN)
