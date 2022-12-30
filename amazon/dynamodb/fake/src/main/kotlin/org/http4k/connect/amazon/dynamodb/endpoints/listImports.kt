package org.http4k.connect.amazon.dynamodb.endpoints

import org.http4k.connect.amazon.AmazonJsonFake
import org.http4k.connect.amazon.dynamodb.action.ListImports
import org.http4k.connect.amazon.dynamodb.action.ListImportsResponse
import org.http4k.connect.amazon.dynamodb.model.ImportSummary
import org.http4k.connect.amazon.dynamodb.model.ImportTableDescription
import org.http4k.connect.storage.Storage

fun AmazonJsonFake.listImports(tableImports: Storage<ImportTableDescription>) = route<ListImports> { listImports ->
    ListImportsResponse(
        ImportSummaryList = tableImports.keySet()
            .map { tableImports[it]!! }
            .filter { it.TableArn == listImports.TableArn }
            .map {
                ImportSummary(ImportArn = it.ImportArn)
            }
    )
}
