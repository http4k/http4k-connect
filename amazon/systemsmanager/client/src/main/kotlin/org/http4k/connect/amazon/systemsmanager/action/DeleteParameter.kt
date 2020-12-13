package org.http4k.connect.amazon.systemsmanager.action

data class DeleteParameter(
    val Name: String
) : SystemsManagerAction<Unit>(Unit::class)
