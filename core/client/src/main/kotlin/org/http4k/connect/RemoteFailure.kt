package org.http4k.connect

import org.http4k.core.Method
import org.http4k.core.Status
import org.http4k.core.Uri

data class RemoteFailure(val method: Method, val uri: Uri, val status: Status, val message: String? = null) {
    fun throwIt(): Nothing = throw Exception(toString())
}
