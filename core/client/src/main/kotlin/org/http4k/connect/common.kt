package org.http4k.connect

import org.http4k.core.Status
import org.http4k.core.Uri

data class RemoteFailure(val uri: Uri, val status: Status) {
    fun throwIt(): Nothing = throw Exception(toString())
}

