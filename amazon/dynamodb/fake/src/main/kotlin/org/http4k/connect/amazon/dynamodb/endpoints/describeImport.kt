package org.http4k.connect.amazon.dynamodb.endpoints

import org.http4k.connect.amazon.AmazonJsonFake
import org.http4k.connect.amazon.dynamodb.action.DescribeImport
import org.http4k.connect.amazon.dynamodb.action.ImportTableResponse
import org.http4k.connect.amazon.dynamodb.model.ImportTableDescription
import org.http4k.connect.storage.Storage

fun AmazonJsonFake.describeImport(tableImports: Storage<ImportTableDescription>) = route<DescribeImport> { describeImport ->
    tableImports[describeImport.ImportArn.value]?.let { ImportTableResponse(it) }?:TODO()
}
