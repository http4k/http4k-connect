package org.http4k.connect.amazon.dynamodb

import com.squareup.moshi.Moshi
import org.http4k.connect.amazon.dynamodb.action.KotshiArchivalSummaryJsonAdapter
import org.http4k.connect.amazon.dynamodb.action.KotshiAttributeDefinitionJsonAdapter
import org.http4k.connect.amazon.dynamodb.action.KotshiAttributeValueJsonAdapter
import org.http4k.connect.amazon.dynamodb.action.KotshiBatchExecuteStatementJsonAdapter
import org.http4k.connect.amazon.dynamodb.action.KotshiBatchGetItemJsonAdapter
import org.http4k.connect.amazon.dynamodb.action.KotshiBatchGetItemsJsonAdapter
import org.http4k.connect.amazon.dynamodb.action.KotshiBatchStatementErrorJsonAdapter
import org.http4k.connect.amazon.dynamodb.action.KotshiBatchStatementsJsonAdapter
import org.http4k.connect.amazon.dynamodb.action.KotshiBatchWriteItemJsonAdapter
import org.http4k.connect.amazon.dynamodb.action.KotshiBatchWriteItemsJsonAdapter
import org.http4k.connect.amazon.dynamodb.action.KotshiBillingModeSummaryJsonAdapter
import org.http4k.connect.amazon.dynamodb.action.KotshiCapacityJsonAdapter
import org.http4k.connect.amazon.dynamodb.action.KotshiConsumedCapacityJsonAdapter
import org.http4k.connect.amazon.dynamodb.action.KotshiCreateTableJsonAdapter
import org.http4k.connect.amazon.dynamodb.action.KotshiDeleteItemJsonAdapter
import org.http4k.connect.amazon.dynamodb.action.KotshiDeleteTableJsonAdapter
import org.http4k.connect.amazon.dynamodb.action.KotshiDescribeTableJsonAdapter
import org.http4k.connect.amazon.dynamodb.action.KotshiDescribedTableJsonAdapter
import org.http4k.connect.amazon.dynamodb.action.KotshiExecuteStatementJsonAdapter
import org.http4k.connect.amazon.dynamodb.action.KotshiExecuteTransactionJsonAdapter
import org.http4k.connect.amazon.dynamodb.action.KotshiExecutedStatementJsonAdapter
import org.http4k.connect.amazon.dynamodb.action.KotshiExecutedTransactionJsonAdapter
import org.http4k.connect.amazon.dynamodb.action.KotshiGetItemJsonAdapter
import org.http4k.connect.amazon.dynamodb.action.KotshiGetItemsResponseItemJsonAdapter
import org.http4k.connect.amazon.dynamodb.action.KotshiGetItemsResponseJsonAdapter
import org.http4k.connect.amazon.dynamodb.action.KotshiGetJsonAdapter
import org.http4k.connect.amazon.dynamodb.action.KotshiGetResponseJsonAdapter
import org.http4k.connect.amazon.dynamodb.action.KotshiGlobalSecondaryIndexCreateJsonAdapter
import org.http4k.connect.amazon.dynamodb.action.KotshiGlobalSecondaryIndexDeleteJsonAdapter
import org.http4k.connect.amazon.dynamodb.action.KotshiGlobalSecondaryIndexJsonAdapter
import org.http4k.connect.amazon.dynamodb.action.KotshiGlobalSecondaryIndexReplicaJsonAdapter
import org.http4k.connect.amazon.dynamodb.action.KotshiGlobalSecondaryIndexResponseJsonAdapter
import org.http4k.connect.amazon.dynamodb.action.KotshiGlobalSecondaryIndexUpdateJsonAdapter
import org.http4k.connect.amazon.dynamodb.action.KotshiGlobalSecondaryIndexUpdatesJsonAdapter
import org.http4k.connect.amazon.dynamodb.action.KotshiGlobalSecondaryIndexesUpdateJsonAdapter
import org.http4k.connect.amazon.dynamodb.action.KotshiItemCollectionMetricsJsonAdapter
import org.http4k.connect.amazon.dynamodb.action.KotshiKeySchemaJsonAdapter
import org.http4k.connect.amazon.dynamodb.action.KotshiListTablesJsonAdapter
import org.http4k.connect.amazon.dynamodb.action.KotshiLocalSecondaryIndexResponseJsonAdapter
import org.http4k.connect.amazon.dynamodb.action.KotshiLocalSecondaryIndexesJsonAdapter
import org.http4k.connect.amazon.dynamodb.action.KotshiModifiedItemJsonAdapter
import org.http4k.connect.amazon.dynamodb.action.KotshiModifiedItemsJsonAdapter
import org.http4k.connect.amazon.dynamodb.action.KotshiParameterizedStatementJsonAdapter
import org.http4k.connect.amazon.dynamodb.action.KotshiProjectionJsonAdapter
import org.http4k.connect.amazon.dynamodb.action.KotshiProvisionedThroughputJsonAdapter
import org.http4k.connect.amazon.dynamodb.action.KotshiProvisionedThroughputOverrideJsonAdapter
import org.http4k.connect.amazon.dynamodb.action.KotshiProvisionedThroughputResponseJsonAdapter
import org.http4k.connect.amazon.dynamodb.action.KotshiPutItemJsonAdapter
import org.http4k.connect.amazon.dynamodb.action.KotshiQueryJsonAdapter
import org.http4k.connect.amazon.dynamodb.action.KotshiQueryResponseJsonAdapter
import org.http4k.connect.amazon.dynamodb.action.KotshiReplicaCreateJsonAdapter
import org.http4k.connect.amazon.dynamodb.action.KotshiReplicaDeleteJsonAdapter
import org.http4k.connect.amazon.dynamodb.action.KotshiReplicaJsonAdapter
import org.http4k.connect.amazon.dynamodb.action.KotshiReplicaUpdateJsonAdapter
import org.http4k.connect.amazon.dynamodb.action.KotshiReplicaUpdatesJsonAdapter
import org.http4k.connect.amazon.dynamodb.action.KotshiReqGetItemJsonAdapter
import org.http4k.connect.amazon.dynamodb.action.KotshiReqStatementJsonAdapter
import org.http4k.connect.amazon.dynamodb.action.KotshiReqWriteItemJsonAdapter
import org.http4k.connect.amazon.dynamodb.action.KotshiRestoreSummaryJsonAdapter
import org.http4k.connect.amazon.dynamodb.action.KotshiSSEDescriptionJsonAdapter
import org.http4k.connect.amazon.dynamodb.action.KotshiSSESpecificationJsonAdapter
import org.http4k.connect.amazon.dynamodb.action.KotshiScanJsonAdapter
import org.http4k.connect.amazon.dynamodb.action.KotshiScanResponseJsonAdapter
import org.http4k.connect.amazon.dynamodb.action.KotshiStatementResponseJsonAdapter
import org.http4k.connect.amazon.dynamodb.action.KotshiStreamSpecificationJsonAdapter
import org.http4k.connect.amazon.dynamodb.action.KotshiTableDescriptionJsonAdapter
import org.http4k.connect.amazon.dynamodb.action.KotshiTableDescriptionResponseJsonAdapter
import org.http4k.connect.amazon.dynamodb.action.KotshiTableListJsonAdapter
import org.http4k.connect.amazon.dynamodb.action.KotshiTransactGetItemJsonAdapter
import org.http4k.connect.amazon.dynamodb.action.KotshiTransactGetItemsJsonAdapter
import org.http4k.connect.amazon.dynamodb.action.KotshiTransactWriteItemJsonAdapter
import org.http4k.connect.amazon.dynamodb.action.KotshiTransactWriteItemsJsonAdapter
import org.http4k.connect.amazon.dynamodb.action.KotshiUpdateItemJsonAdapter
import org.http4k.connect.amazon.dynamodb.action.KotshiUpdateTableJsonAdapter
import org.http4k.connect.amazon.model.AttributeName
import org.http4k.connect.amazon.model.IndexName
import org.http4k.connect.amazon.model.TableName
import org.http4k.format.AwsJsonAdapterFactory
import org.http4k.format.ConfigurableMoshi
import org.http4k.format.adapter
import org.http4k.format.asConfigurable
import org.http4k.format.value
import org.http4k.format.withAwsCoreMappings
import org.http4k.format.withStandardMappings

object DynamoDbMoshi : ConfigurableMoshi(
    Moshi.Builder()
        .add(DynamoDbJsonAdapterFactory)
        .asConfigurable()
        .withStandardMappings()
        .withAwsCoreMappings()
        .value(AttributeName)
        .value(IndexName)
        .value(TableName)
        .done()
)

object DynamoDbJsonAdapterFactory : AwsJsonAdapterFactory(
    // table actions
    adapter(::KotshiCreateTableJsonAdapter),
    adapter(::KotshiDeleteTableJsonAdapter),
    adapter(::KotshiDescribeTableJsonAdapter),
    adapter(::KotshiListTablesJsonAdapter),
    adapter(::KotshiUpdateTableJsonAdapter),

    // item actions
    adapter(::KotshiDeleteItemJsonAdapter),
    adapter(::KotshiGetItemJsonAdapter),
    adapter(::KotshiPutItemJsonAdapter),
    adapter(::KotshiQueryJsonAdapter),
    adapter(::KotshiScanJsonAdapter),
    adapter(::KotshiTransactGetItemsJsonAdapter),
    adapter(::KotshiTransactWriteItemsJsonAdapter),
    adapter(::KotshiUpdateItemJsonAdapter),

    // Batch
    adapter(::KotshiBatchGetItemJsonAdapter),
    adapter(::KotshiBatchWriteItemJsonAdapter),

    // PartiSQL
    adapter(::KotshiExecuteTransactionJsonAdapter),
    adapter(::KotshiExecuteStatementJsonAdapter),
    adapter(::KotshiBatchExecuteStatementJsonAdapter),

    // model
    adapter(::KotshiArchivalSummaryJsonAdapter),
    adapter(::KotshiAttributeDefinitionJsonAdapter),
    adapter(::KotshiAttributeValueJsonAdapter),
    adapter(::KotshiBatchGetItemsJsonAdapter),
    adapter(::KotshiBatchWriteItemsJsonAdapter),
    adapter(::KotshiBatchStatementsJsonAdapter),
    adapter(::KotshiBatchStatementErrorJsonAdapter),
    adapter(::KotshiBillingModeSummaryJsonAdapter),
    adapter { KotshiCapacityJsonAdapter() },
    adapter(::KotshiConsumedCapacityJsonAdapter),
    adapter(::KotshiDescribedTableJsonAdapter),
    adapter(::KotshiExecutedTransactionJsonAdapter),
    adapter(::KotshiExecutedStatementJsonAdapter),
    adapter(::KotshiGetJsonAdapter),
    adapter(::KotshiGetResponseJsonAdapter),
    adapter(::KotshiGetItemsResponseJsonAdapter),
    adapter(::KotshiGetItemsResponseItemJsonAdapter),
    adapter(::KotshiGlobalSecondaryIndexCreateJsonAdapter),
    adapter(::KotshiGlobalSecondaryIndexDeleteJsonAdapter),
    adapter(::KotshiGlobalSecondaryIndexesUpdateJsonAdapter),
    adapter(::KotshiGlobalSecondaryIndexJsonAdapter),
    adapter(::KotshiGlobalSecondaryIndexReplicaJsonAdapter),
    adapter(::KotshiGlobalSecondaryIndexResponseJsonAdapter),
    adapter(::KotshiGlobalSecondaryIndexUpdateJsonAdapter),
    adapter(::KotshiGlobalSecondaryIndexUpdatesJsonAdapter),
    adapter(::KotshiItemCollectionMetricsJsonAdapter),
    adapter(::KotshiKeySchemaJsonAdapter),
    adapter(::KotshiLocalSecondaryIndexesJsonAdapter),
    adapter(::KotshiLocalSecondaryIndexResponseJsonAdapter),
    adapter(::KotshiModifiedItemJsonAdapter),
    adapter(::KotshiModifiedItemsJsonAdapter),
    adapter(::KotshiParameterizedStatementJsonAdapter),
    adapter(::KotshiProjectionJsonAdapter),
    adapter { KotshiProvisionedThroughputJsonAdapter() },
    adapter { KotshiProvisionedThroughputOverrideJsonAdapter() },
    adapter(::KotshiProvisionedThroughputResponseJsonAdapter),
    adapter(::KotshiQueryResponseJsonAdapter),
    adapter(::KotshiReplicaCreateJsonAdapter),
    adapter(::KotshiReplicaDeleteJsonAdapter),
    adapter(::KotshiReplicaJsonAdapter),
    adapter(::KotshiReplicaUpdateJsonAdapter),
    adapter(::KotshiReplicaUpdatesJsonAdapter),
    adapter(::KotshiReqGetItemJsonAdapter),
    adapter(::KotshiReqWriteItemJsonAdapter),
    adapter(::KotshiReqStatementJsonAdapter),
    adapter(::KotshiRestoreSummaryJsonAdapter),
    adapter(::KotshiScanResponseJsonAdapter),
    adapter(::KotshiSSEDescriptionJsonAdapter),
    adapter(::KotshiSSESpecificationJsonAdapter),
    adapter(::KotshiStatementResponseJsonAdapter),
    adapter(::KotshiStreamSpecificationJsonAdapter),
    adapter(::KotshiTableDescriptionJsonAdapter),
    adapter(::KotshiTableDescriptionResponseJsonAdapter),
    adapter(::KotshiTableListJsonAdapter),
    adapter(::KotshiTransactGetItemJsonAdapter),
    adapter(::KotshiTransactWriteItemJsonAdapter),
)
