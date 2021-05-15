package org.http4k.connect.amazon.model

import dev.forkhandles.values.StringValueFactory
import dev.forkhandles.values.minLength
import org.http4k.connect.amazon.core.model.ARN
import org.http4k.connect.amazon.core.model.Base64Blob
import org.http4k.connect.amazon.core.model.ResourceId
import se.ansman.kotshi.JsonSerializable

class DeliveryStreamName private constructor(value: String) : ResourceId(value) {
    companion object : StringValueFactory<DeliveryStreamName>(::DeliveryStreamName, 1.minLength)
}

@JsonSerializable
data class Record(
    val Data: Base64Blob
)


@JsonSerializable
data class DeliveryStreamEncryptionConfigurationInput(
    val KeyARN: ARN?,
    val KeyType: String?
)

@JsonSerializable
data class Parameters(
    val ParameterName: String?,
    val ParameterValue: String?
)

@JsonSerializable
data class Processor(
    val Parameters: List<Parameters>?,
    val Type: String?
)

@JsonSerializable
data class VpcConfiguration(
    val RoleARN: ARN?,
    val SecurityGroupIds: List<String>?,
    val SubnetIds: List<String>?
)

@JsonSerializable
data class ElasticsearchDestinationConfiguration(
    val BufferingHints: BufferingHints?,
    val CloudWatchLoggingOptions: CloudWatchLoggingOptions?,
    val ClusterEndpoint: String?,
    val DomainARN: ARN?,
    val IndexName: String?,
    val IndexRotationPeriod: String?,
    val ProcessingConfiguration: ProcessingConfiguration?,
    val RetryOptions: RetryOptions?,
    val RoleARN: ARN?,
    val S3BackupMode: String?,
    val S3Configuration: S3Configuration?,
    val TypeName: String?,
    val VpcConfiguration: VpcConfiguration?
)

@JsonSerializable
data class HiveJsonSerDe(
    val TimestampFormats: List<String>?
)

@JsonSerializable
data class ColumnToJsonKeyMappings(
    val string: String?
)

@JsonSerializable
data class OpenXJsonSerDe(
    val CaseInsensitive: Boolean?,
    val ColumnToJsonKeyMappings: ColumnToJsonKeyMappings?,
    val ConvertDotsInJsonKeysToUnderscores: Boolean?
)

@JsonSerializable
data class Deserializer(
    val HiveJsonSerDe: HiveJsonSerDe?,
    val OpenXJsonSerDe: OpenXJsonSerDe?
)

@JsonSerializable
data class InputFormatConfiguration(
    val Deserializer: Deserializer?
)

@JsonSerializable
data class OrcSerDe(
    val BlockSizeBytes: Int?,
    val BloomFilterColumns: List<String>?,
    val BloomFilterFalsePositiveProbability: Int?,
    val Compression: String?,
    val DictionaryKeyThreshold: Int?,
    val EnablePadding: Boolean?,
    val FormatVersion: String?,
    val PaddingTolerance: Int?,
    val RowIndexStride: Int?,
    val StripeSizeBytes: Int?
)

@JsonSerializable
data class ParquetSerDe(
    val BlockSizeBytes: Int?,
    val Compression: String?,
    val EnableDictionaryCompression: Boolean?,
    val MaxPaddingBytes: Int?,
    val PageSizeBytes: Int?,
    val WriterVersion: String?
)

@JsonSerializable
data class Serializer(
    val OrcSerDe: OrcSerDe?,
    val ParquetSerDe: ParquetSerDe?
)

@JsonSerializable
data class OutputFormatConfiguration(
    val Serializer: Serializer?
)

@JsonSerializable
data class SchemaConfiguration(
    val CatalogId: String?,
    val DatabaseName: String?,
    val Region: String?,
    val RoleARN: ARN?,
    val TableName: String?,
    val VersionId: String?
)

@JsonSerializable
data class DataFormatConversionConfiguration(
    val Enabled: Boolean?,
    val InputFormatConfiguration: InputFormatConfiguration?,
    val OutputFormatConfiguration: OutputFormatConfiguration?,
    val SchemaConfiguration: SchemaConfiguration?
)

@JsonSerializable
data class ExtendedS3DestinationConfiguration(
    val BucketARN: ARN?,
    val BufferingHints: BufferingHints?,
    val CloudWatchLoggingOptions: CloudWatchLoggingOptions?,
    val CompressionFormat: String?,
    val DataFormatConversionConfiguration: DataFormatConversionConfiguration?,
    val EncryptionConfiguration: EncryptionConfiguration?,
    val ErrorOutputPrefix: String?,
    val Prefix: String?,
    val ProcessingConfiguration: ProcessingConfiguration?,
    val RoleARN: ARN?,
    val S3BackupConfiguration: S3BackupConfiguration?,
    val S3BackupMode: String?
)

@JsonSerializable
data class EndpointConfiguration(
    val AccessKey: String?,
    val Name: String?,
    val Url: String?
)

@JsonSerializable
data class CommonAttributes(
    val AttributeName: String?,
    val AttributeValue: String?
)

@JsonSerializable
data class RequestConfiguration(
    val CommonAttributes: List<CommonAttributes>?,
    val ContentEncoding: String?
)

@JsonSerializable
data class HttpEndpointDestinationConfiguration(
    val BufferingHints: BufferingHints?,
    val CloudWatchLoggingOptions: CloudWatchLoggingOptions?,
    val EndpointConfiguration: EndpointConfiguration?,
    val ProcessingConfiguration: ProcessingConfiguration?,
    val RequestConfiguration: RequestConfiguration?,
    val RetryOptions: RetryOptions?,
    val RoleARN: ARN?,
    val S3BackupMode: String?,
    val S3Configuration: S3Configuration?
)

@JsonSerializable
data class KinesisStreamSourceConfiguration(
    val KinesisStreamARN: ARN?,
    val RoleARN: String?
)

@JsonSerializable
data class CopyCommand(
    val CopyOptions: String?,
    val DataTableColumns: String?,
    val DataTableName: String?
)

@JsonSerializable
data class RedshiftDestinationConfiguration(
    val CloudWatchLoggingOptions: CloudWatchLoggingOptions?,
    val ClusterJDBCURL: String?,
    val CopyCommand: CopyCommand?,
    val Password: String?,
    val ProcessingConfiguration: ProcessingConfiguration?,
    val RetryOptions: RetryOptions?,
    val RoleARN: ARN?,
    val S3BackupConfiguration: S3BackupConfiguration?,
    val S3BackupMode: String?,
    val S3Configuration: S3Configuration?,
    val Username: String?
)

@JsonSerializable
data class S3DestinationConfiguration(
    val BucketARN: ARN,
    val BufferingHints: BufferingHints?,
    val CloudWatchLoggingOptions: CloudWatchLoggingOptions?,
    val CompressionFormat: String?,
    val EncryptionConfiguration: EncryptionConfiguration?,
    val ErrorOutputPrefix: String?,
    val Prefix: String?,
    val RoleARN: String?
)

@JsonSerializable
data class SplunkDestinationConfiguration(
    val CloudWatchLoggingOptions: CloudWatchLoggingOptions?,
    val HECAcknowledgmentTimeoutInSeconds: Int?,
    val HECEndpoint: String?,
    val HECEndpointType: String?,
    val HECToken: String?,
    val ProcessingConfiguration: ProcessingConfiguration?,
    val RetryOptions: RetryOptions?,
    val S3BackupMode: String?,
    val S3Configuration: S3Configuration?
)

@JsonSerializable
data class BufferingHints(
    val IntervalInSeconds: Int?,
    val SizeInMBs: Int?
)

@JsonSerializable
data class KMSEncryptionConfig(
    val AWSKMSKeyARN: String?
)

@JsonSerializable
data class EncryptionConfiguration(
    val KMSEncryptionConfig: KMSEncryptionConfig?,
    val NoEncryptionConfig: String?
)

@JsonSerializable
data class CloudWatchLoggingOptions(
    val Enabled: Boolean?,
    val LogGroupName: String?,
    val LogStreamName: String?
)

@JsonSerializable
data class RetryOptions(
    val DurationInSeconds: Int?
)

@JsonSerializable
data class S3Configuration(
    val BucketARN: ARN?,
    val BufferingHints: BufferingHints?,
    val CloudWatchLoggingOptions: CloudWatchLoggingOptions?,
    val CompressionFormat: String?,
    val EncryptionConfiguration: EncryptionConfiguration?,
    val ErrorOutputPrefix: String?,
    val Prefix: String?,
    val RoleARN: String?
)

@JsonSerializable
data class ProcessingConfiguration(
    val Enabled: Boolean?,
    val Processors: List<Processor>?
)

@JsonSerializable
data class S3BackupConfiguration(
    val BucketARN: ARN?,
    val BufferingHints: BufferingHints?,
    val CloudWatchLoggingOptions: CloudWatchLoggingOptions?,
    val CompressionFormat: String?,
    val EncryptionConfiguration: EncryptionConfiguration?,
    val ErrorOutputPrefix: String?,
    val Prefix: String?,
    val RoleARN: String?
)

@JsonSerializable
data class RequestResponses(
    val ErrorCode: String?,
    val ErrorMessage: String?,
    val RecordId: String
)
