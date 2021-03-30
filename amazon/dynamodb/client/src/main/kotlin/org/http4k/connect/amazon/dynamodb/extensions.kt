package org.http4k.connect.amazon.dynamodb

import org.http4k.connect.amazon.dynamodb.action.Scan
import org.http4k.connect.amazon.paginated

fun DynamoDb.scanPaginate(scan: Scan) = paginated(::invoke, scan)
