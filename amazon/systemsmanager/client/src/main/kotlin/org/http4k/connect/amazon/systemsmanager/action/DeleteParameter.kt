package org.http4k.connect.amazon.systemsmanager.action

import org.http4k.connect.Http4kConnectAction

@Http4kConnectAction
data class DeleteParameter(val Name: String) : SystemsManagerAction<Unit>(Unit::class)
