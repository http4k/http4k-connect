package org.http4k.filter

import org.http4k.cloudnative.env.Secret
import org.http4k.connect.github.filter.HmacSha256.hmacSHA256
import org.http4k.core.Filter
import org.http4k.core.with
import org.http4k.lens.Header
import org.http4k.lens.X_HUB_SIGNATURE_256

fun ClientFilters.SignGitHubCallbackSha256(secret: () -> Secret) = Filter { next ->
    {
        next(
            it.with(
                Header.X_HUB_SIGNATURE_256 of secret().use { s -> hmacSHA256(s.toByteArray(), it.bodyString()) }.toHexString()
            )
        )
    }
}

private fun ByteArray.toHexString() = joinToString("") { String.format("%02x", (it.toInt() and 0xFF)) }
