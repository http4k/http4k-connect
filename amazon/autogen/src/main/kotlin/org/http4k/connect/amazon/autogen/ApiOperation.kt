package org.http4k.connect.amazon.autogen

data class ApiOperation(
    val name: OperationName,
    val http: Http,
    val input: ShapeRef?,
    val output: ShapeRef?,
    val errors: List<ShapeRef>? = null,
    val documentation: Documentation?
)
