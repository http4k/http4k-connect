package org.http4k.connect.amazon.model

import dev.forkhandles.values.StringValueFactory
import dev.forkhandles.values.minLength
import org.http4k.connect.amazon.core.model.ARN
import org.http4k.connect.amazon.core.model.Base64Blob
import org.http4k.connect.amazon.core.model.ResourceId
import org.http4k.core.Uri
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
    val KeyARN: ARN? = null,
    val KeyType: String? = null
)

@JsonSerializable
data class Parameters(
    val ParameterName: String? = null,
    val ParameterValue: String? = null
)

@JsonSerializable
data class Processor(
    val Parameters: List<Parameters>? = null,
    val Type: String? = null
)

@JsonSerializable
data class VpcConfiguration(
    val RoleARN: ARN? = null,
    val SecurityGroupIds: List<String>? = null,
    val SubnetIds: List<String>? = null
)

@JsonSerializable
data class ElasticsearchDestinationConfiguration(
    val BufferingHints: BufferingHints? = null,
    val CloudWatchLoggingOptions: CloudWatchLoggingOptions? = null,
    val ClusterEndpoint: String? = null,
    val DomainARN: ARN? = null,
    val IndexName: String? = null,
    val IndexRotationPeriod: String? = null,
    val ProcessingConfiguration: ProcessingConfiguration? = null,
    val RetryOptions: RetryOptions? = null,
    val RoleARN: ARN? = null,
    val S3BackupMode: String? = null,
    val S3Configuration: S3Configuration? = null,
    val TypeName: String? = null,
    val VpcConfiguration: VpcConfiguration? = null
)

@JsonSerializable
data class HiveJsonSerDe(
    val TimestampFormats: List<String>? = null
)

@JsonSerializable
data class OpenXJsonSerDe(
    val CaseInsensitive: Boolean? = null,
    val ColumnToJsonKeyMappings: Map<String, String>? = null,
    val ConvertDotsInJsonKeysToUnderscores: Boolean? = null
)

@JsonSerializable
data class Deserializer(
    val HiveJsonSerDe: HiveJsonSerDe? = null,
    val OpenXJsonSerDe: OpenXJsonSerDe? = null
)

@JsonSerializable
data class InputFormatConfiguration(
    val Deserializer: Deserializer? = null
)

@JsonSerializable
data class OrcSerDe(
    val BlockSizeBytes: Int? = null,
    val BloomFilterColumns: List<String>? = null,
    val BloomFilterFalsePositiveProbability: Int? = null,
    val Compression: String? = null,
    val DictionaryKeyThreshold: Int? = null,
    val EnablePadding: Boolean? = null,
    val FormatVersion: String? = null,
    val PaddingTolerance: Int? = null,
    val RowIndexStride: Int? = null,
    val StripeSizeBytes: Int? = null
)

@JsonSerializable
data class ParquetSerDe(
    val BlockSizeBytes: Int? = null,
    val Compression: String? = null,
    val EnableDictionaryCompression: Boolean? = null,
    val MaxPaddingBytes: Int? = null,
    val PageSizeBytes: Int? = null,
    val WriterVersion: String? = null
)

@JsonSerializable
data class Serializer(
    val OrcSerDe: OrcSerDe? = null,
    val ParquetSerDe: ParquetSerDe? = null
)

@JsonSerializable
data class OutputFormatConfiguration(
    val Serializer: Serializer? = null
)

@JsonSerializable
data class SchemaConfiguration(
    val CatalogId: String? = null,
    val DatabaseName: String? = null,
    val Region: String? = null,
    val RoleARN: ARN? = null,
    val TableName: String? = null,
    val VersionId: String? = null
)

@JsonSerializable
data class DataFormatConversionConfiguration(
    val Enabled: Boolean? = null,
    val InputFormatConfiguration: InputFormatConfiguration? = null,
    val OutputFormatConfiguration: OutputFormatConfiguration? = null,
    val SchemaConfiguration: SchemaConfiguration? = null
)

@JsonSerializable
data class ExtendedS3DestinationConfiguration(
    val BucketARN: ARN? = null,
    val BufferingHints: BufferingHints? = null,
    val CloudWatchLoggingOptions: CloudWatchLoggingOptions? = null,
    val CompressionFormat: String? = null,
    val DataFormatConversionConfiguration: DataFormatConversionConfiguration? = null,
    val EncryptionConfiguration: EncryptionConfiguration? = null,
    val ErrorOutputPrefix: String? = null,
    val Prefix: String? = null,
    val ProcessingConfiguration: ProcessingConfiguration? = null,
    val RoleARN: ARN? = null,
    val S3BackupConfiguration: S3BackupConfiguration? = null,
    val S3BackupMode: String? = null
)

@JsonSerializable
data class EndpointConfiguration(
    val AccessKey: String? = null,
    val Name: String? = null,
    val Url: Uri? = null
)

@JsonSerializable
data class CommonAttributes(
    val AttributeName: String? = null,
    val AttributeValue: String? = null
)

@JsonSerializable
data class RequestConfiguration(
    val CommonAttributes: List<CommonAttributes>? = null,
    val ContentEncoding: String? = null
)

@JsonSerializable
data class HttpEndpointDestinationConfiguration(
    val BufferingHints: BufferingHints? = null,
    val CloudWatchLoggingOptions: CloudWatchLoggingOptions? = null,
    val EndpointConfiguration: EndpointConfiguration? = null,
    val ProcessingConfiguration: ProcessingConfiguration? = null,
    val RequestConfiguration: RequestConfiguration? = null,
    val RetryOptions: RetryOptions? = null,
    val RoleARN: ARN? = null,
    val S3BackupMode: String? = null,
    val S3Configuration: S3Configuration? = null
)

@JsonSerializable
data class KinesisStreamSourceConfiguration(
    val KinesisStreamARN: ARN? = null,
    val RoleARN: ARN? = null
)

@JsonSerializable
data class CopyCommand(
    val CopyOptions: String? = null,
    val DataTableColumns: String? = null,
    val DataTableName: String? = null
)

@JsonSerializable
data class RedshiftDestinationConfiguration(
    val CloudWatchLoggingOptions: CloudWatchLoggingOptions? = null,
    val ClusterJDBCURL: String? = null,
    val CopyCommand: CopyCommand? = null,
    val Password: String? = null,
    val ProcessingConfiguration: ProcessingConfiguration? = null,
    val RetryOptions: RetryOptions? = null,
    val RoleARN: ARN? = null,
    val S3BackupConfiguration: S3BackupConfiguration? = null,
    val S3BackupMode: String? = null,
    val S3Configuration: S3Configuration? = null,
    val Username: String? = null
)

@JsonSerializable
data class S3DestinationConfiguration(
    val BucketARN: ARN,
    val BufferingHints: BufferingHints? = null,
    val CloudWatchLoggingOptions: CloudWatchLoggingOptions? = null,
    val CompressionFormat: String? = null,
    val EncryptionConfiguration: EncryptionConfiguration? = null,
    val ErrorOutputPrefix: String? = null,
    val Prefix: String? = null,
    val RoleARN: ARN? = null
)

@JsonSerializable
data class SplunkDestinationConfiguration(
    val CloudWatchLoggingOptions: CloudWatchLoggingOptions? = null,
    val HECAcknowledgmentTimeoutInSeconds: Int? = null,
    val HECEndpoint: String? = null,
    val HECEndpointType: String? = null,
    val HECToken: String? = null,
    val ProcessingConfiguration: ProcessingConfiguration? = null,
    val RetryOptions: RetryOptions? = null,
    val S3BackupMode: String? = null,
    val S3Configuration: S3Configuration? = null
)

@JsonSerializable
data class BufferingHints(
    val IntervalInSeconds: Int? = null,
    val SizeInMBs: Int? = null
)

@JsonSerializable
data class KMSEncryptionConfig(
    val AWSKMSKeyARN: ARN? = null
)

@JsonSerializable
data class EncryptionConfiguration(
    val KMSEncryptionConfig: KMSEncryptionConfig? = null,
    val NoEncryptionConfig: String? = null
)

@JsonSerializable
data class CloudWatchLoggingOptions(
    val Enabled: Boolean? = null,
    val LogGroupName: String? = null,
    val LogStreamName: String? = null
)

@JsonSerializable
data class RetryOptions(
    val DurationInSeconds: Int? = null
)

@JsonSerializable
data class S3Configuration(
    val BucketARN: ARN? = null,
    val BufferingHints: BufferingHints? = null,
    val CloudWatchLoggingOptions: CloudWatchLoggingOptions? = null,
    val CompressionFormat: String? = null,
    val EncryptionConfiguration: EncryptionConfiguration? = null,
    val ErrorOutputPrefix: String? = null,
    val Prefix: String? = null,
    val RoleARN: ARN? = null
)

@JsonSerializable
data class ProcessingConfiguration(
    val Enabled: Boolean? = null,
    val Processors: List<Processor>? = null
)

@JsonSerializable
data class S3BackupConfiguration(
    val BucketARN: ARN? = null,
    val BufferingHints: BufferingHints? = null,
    val CloudWatchLoggingOptions: CloudWatchLoggingOptions? = null,
    val CompressionFormat: String? = null,
    val EncryptionConfiguration: EncryptionConfiguration? = null,
    val ErrorOutputPrefix: String? = null,
    val Prefix: String? = null,
    val RoleARN: ARN? = null
)

@JsonSerializable
data class RequestResponses(
    val ErrorCode: String? = null,
    val ErrorMessage: String? = null,
    val RecordId: String? = null
)
