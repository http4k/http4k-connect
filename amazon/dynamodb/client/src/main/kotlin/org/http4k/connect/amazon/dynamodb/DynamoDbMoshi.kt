package org.http4k.connect.amazon.dynamodb

import com.squareup.moshi.Moshi
import org.http4k.connect.amazon.dynamodb.action.KotshiAttributeValueJsonAdapter
import org.http4k.connect.amazon.dynamodb.action.KotshiBatchGetItemJsonAdapter
import org.http4k.connect.amazon.dynamodb.action.KotshiBatchItemsJsonAdapter
import org.http4k.connect.amazon.dynamodb.action.KotshiBatchWriteItemJsonAdapter
import org.http4k.connect.amazon.dynamodb.action.KotshiCapacityJsonAdapter
import org.http4k.connect.amazon.dynamodb.action.KotshiConditionCheckJsonAdapter
import org.http4k.connect.amazon.dynamodb.action.KotshiConsumedCapacityJsonAdapter
import org.http4k.connect.amazon.dynamodb.action.KotshiCreateTableJsonAdapter
import org.http4k.connect.amazon.dynamodb.action.KotshiDeleteItemJsonAdapter
import org.http4k.connect.amazon.dynamodb.action.KotshiDeleteJsonAdapter
import org.http4k.connect.amazon.dynamodb.action.KotshiDeleteTableJsonAdapter
import org.http4k.connect.amazon.dynamodb.action.KotshiDescribeTableJsonAdapter
import org.http4k.connect.amazon.dynamodb.action.KotshiDescribedTableJsonAdapter
import org.http4k.connect.amazon.dynamodb.action.KotshiExecuteStatementJsonAdapter
import org.http4k.connect.amazon.dynamodb.action.KotshiExecuteTransactionJsonAdapter
import org.http4k.connect.amazon.dynamodb.action.KotshiExecutedStatementJsonAdapter
import org.http4k.connect.amazon.dynamodb.action.KotshiExecutedTransactionJsonAdapter
import org.http4k.connect.amazon.dynamodb.action.KotshiGetItemJsonAdapter
import org.http4k.connect.amazon.dynamodb.action.KotshiGetJsonAdapter
import org.http4k.connect.amazon.dynamodb.action.KotshiGetResponseJsonAdapter
import org.http4k.connect.amazon.dynamodb.action.KotshiItemCollectionMetricsJsonAdapter
import org.http4k.connect.amazon.dynamodb.action.KotshiListTablesJsonAdapter
import org.http4k.connect.amazon.dynamodb.action.KotshiModifiedItemJsonAdapter
import org.http4k.connect.amazon.dynamodb.action.KotshiModifiedItemsJsonAdapter
import org.http4k.connect.amazon.dynamodb.action.KotshiParameterizedStatementJsonAdapter
import org.http4k.connect.amazon.dynamodb.action.KotshiPutItemJsonAdapter
import org.http4k.connect.amazon.dynamodb.action.KotshiPutJsonAdapter
import org.http4k.connect.amazon.dynamodb.action.KotshiPutRequestJsonAdapter
import org.http4k.connect.amazon.dynamodb.action.KotshiQueryJsonAdapter
import org.http4k.connect.amazon.dynamodb.action.KotshiQueryResponseJsonAdapter
import org.http4k.connect.amazon.dynamodb.action.KotshiReqGetItemJsonAdapter
import org.http4k.connect.amazon.dynamodb.action.KotshiReqWriteItemJsonAdapter
import org.http4k.connect.amazon.dynamodb.action.KotshiTableListJsonAdapter
import org.http4k.connect.amazon.dynamodb.action.KotshiTransactGetItemJsonAdapter
import org.http4k.connect.amazon.dynamodb.action.KotshiTransactWriteItemJsonAdapter
import org.http4k.connect.amazon.dynamodb.action.KotshiTransactWriteItemsJsonAdapter
import org.http4k.connect.amazon.dynamodb.action.KotshiUpdateItemJsonAdapter
import org.http4k.connect.amazon.dynamodb.action.KotshiUpdateJsonAdapter
import org.http4k.connect.amazon.dynamodb.action.KotshiUpdateTableJsonAdapter
import org.http4k.connect.amazon.model.AttributeName
import org.http4k.connect.amazon.model.IndexName
import org.http4k.connect.amazon.model.KotshiArchivalSummaryJsonAdapter
import org.http4k.connect.amazon.model.KotshiAttributeDefinitionJsonAdapter
import org.http4k.connect.amazon.model.KotshiBillingModeSummaryJsonAdapter
import org.http4k.connect.amazon.model.KotshiGlobalSecondaryIndexCreateJsonAdapter
import org.http4k.connect.amazon.model.KotshiGlobalSecondaryIndexDeleteJsonAdapter
import org.http4k.connect.amazon.model.KotshiGlobalSecondaryIndexJsonAdapter
import org.http4k.connect.amazon.model.KotshiGlobalSecondaryIndexReplicaJsonAdapter
import org.http4k.connect.amazon.model.KotshiGlobalSecondaryIndexResponseJsonAdapter
import org.http4k.connect.amazon.model.KotshiGlobalSecondaryIndexUpdateJsonAdapter
import org.http4k.connect.amazon.model.KotshiGlobalSecondaryIndexUpdatesJsonAdapter
import org.http4k.connect.amazon.model.KotshiGlobalSecondaryIndexesUpdateJsonAdapter
import org.http4k.connect.amazon.model.KotshiKeySchemaJsonAdapter
import org.http4k.connect.amazon.model.KotshiLocalSecondaryIndexResponseJsonAdapter
import org.http4k.connect.amazon.model.KotshiLocalSecondaryIndexesJsonAdapter
import org.http4k.connect.amazon.model.KotshiProjectionJsonAdapter
import org.http4k.connect.amazon.model.KotshiProvisionedThroughputJsonAdapter
import org.http4k.connect.amazon.model.KotshiProvisionedThroughputOverrideJsonAdapter
import org.http4k.connect.amazon.model.KotshiProvisionedThroughputResponseJsonAdapter
import org.http4k.connect.amazon.model.KotshiReplicaCreateJsonAdapter
import org.http4k.connect.amazon.model.KotshiReplicaDeleteJsonAdapter
import org.http4k.connect.amazon.model.KotshiReplicaJsonAdapter
import org.http4k.connect.amazon.model.KotshiReplicaUpdateJsonAdapter
import org.http4k.connect.amazon.model.KotshiReplicaUpdatesJsonAdapter
import org.http4k.connect.amazon.model.KotshiRestoreSummaryJsonAdapter
import org.http4k.connect.amazon.model.KotshiSSEDescriptionJsonAdapter
import org.http4k.connect.amazon.model.KotshiSSESpecificationJsonAdapter
import org.http4k.connect.amazon.model.KotshiStreamSpecificationJsonAdapter
import org.http4k.connect.amazon.model.KotshiTableDescriptionJsonAdapter
import org.http4k.connect.amazon.model.KotshiTableDescriptionResponseJsonAdapter
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
    adapter(::KotshiUpdateItemJsonAdapter),
    adapter(::KotshiTransactWriteItemsJsonAdapter),
    adapter(::KotshiConditionCheckJsonAdapter),
    adapter(::KotshiDeleteJsonAdapter),
    adapter(::KotshiPutJsonAdapter),
    adapter(::KotshiUpdateJsonAdapter),

    // Batch
    adapter(::KotshiBatchGetItemJsonAdapter),
    adapter(::KotshiBatchWriteItemJsonAdapter),

    // PartiSQL
    adapter(::KotshiExecuteTransactionJsonAdapter),
    adapter(::KotshiExecuteStatementJsonAdapter),

    // model
    adapter(::KotshiArchivalSummaryJsonAdapter),
    adapter(::KotshiAttributeDefinitionJsonAdapter),
    adapter(::KotshiAttributeValueJsonAdapter),
    adapter(::KotshiBatchItemsJsonAdapter),
    adapter(::KotshiBillingModeSummaryJsonAdapter),
    adapter { KotshiCapacityJsonAdapter() },
    adapter(::KotshiConsumedCapacityJsonAdapter),
    adapter(::KotshiDescribedTableJsonAdapter),
    adapter(::KotshiExecutedTransactionJsonAdapter),
    adapter(::KotshiExecutedStatementJsonAdapter),
    adapter(::KotshiGetJsonAdapter),
    adapter(::KotshiGetResponseJsonAdapter),
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
    adapter(::KotshiPutRequestJsonAdapter),
    adapter(::KotshiRestoreSummaryJsonAdapter),
    adapter(::KotshiSSEDescriptionJsonAdapter),
    adapter(::KotshiSSESpecificationJsonAdapter),
    adapter(::KotshiStreamSpecificationJsonAdapter),
    adapter(::KotshiTableDescriptionJsonAdapter),
    adapter(::KotshiTableDescriptionResponseJsonAdapter),
    adapter(::KotshiTableListJsonAdapter),
    adapter(::KotshiTransactGetItemJsonAdapter),
    adapter(::KotshiTransactWriteItemJsonAdapter),
)
