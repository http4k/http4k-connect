package org.http4k.connect.amazon.autogen

data class AwsServiceApi(
    val version: String?,
    val metadata: Metadata,
    val operations: Map<OperationName, ApiOperation>,
    val shapes: Map<ShapeName, Shape>,
    val documentation: Documentation?
)
