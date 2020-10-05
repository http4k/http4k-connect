package org.http4k.core

/**
 * This will exist in http4k in the next release.
 */
object SetAuthorityFrom {
    operator fun invoke(uri: Uri): Filter = Filter { next ->
        { request ->
            next(request.uri(request.uri.authority(uri.authority).scheme(uri.scheme)))
        }
    }
}
