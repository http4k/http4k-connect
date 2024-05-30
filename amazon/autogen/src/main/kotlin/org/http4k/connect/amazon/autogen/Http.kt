package org.http4k.connect.amazon.autogen

import org.http4k.core.Method
import org.http4k.core.Uri

data class Http(
    val method: Method,
    val requestUri: Uri
)
