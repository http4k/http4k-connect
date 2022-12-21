package org.http4k.connect.amazon.dynamodb.endpoints

import org.http4k.connect.amazon.AmazonJsonFake
import org.http4k.connect.amazon.core.model.ARN
import org.http4k.connect.amazon.core.model.AwsAccount
import org.http4k.connect.amazon.core.model.AwsService
import org.http4k.connect.amazon.core.model.Region
import org.http4k.connect.amazon.core.model.ResourceId
import org.http4k.connect.amazon.dynamodb.DynamoDb
import org.http4k.connect.amazon.dynamodb.DynamoTable
import org.http4k.connect.amazon.dynamodb.action.ImportTable
import org.http4k.connect.amazon.dynamodb.action.TableDescriptionResponse
import org.http4k.connect.amazon.dynamodb.action.UpdateTable
import org.http4k.connect.amazon.dynamodb.model.ImportTableDescription
import org.http4k.connect.storage.Storage

fun AmazonJsonFake.importTable(tableImports: Storage<ImportTableDescription>) = route<ImportTable> { import ->
//    val arn = ARN.of(DynamoDb.awsService, Region.EU_WEST_2, AwsAccount.of("0"), "1234") 
//    tableImports[import.]
    TODO()
}

