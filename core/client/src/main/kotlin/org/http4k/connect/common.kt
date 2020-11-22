package org.http4k.connect

import org.http4k.core.Status
import org.http4k.core.Uri

data class RemoteFailure(val uri: Uri, val status: Status) {
    fun throwIt(): Nothing = throw Exception(toString())
}

sealed class Choice2<out A, out B> {
    data class _1<T>(val value: T) : Choice2<T, Nothing>()
    data class _2<T>(val value: T) : Choice2<Nothing, T>()

    fun as1() = if (this is _1) value else null
    fun as2() = if (this is _2) value else null
}

sealed class Choice3<A, B, C> {
    data class _1<T>(val value: T) : Choice3<T, Nothing, Nothing>()
    data class _2<T>(val value: T) : Choice3<Nothing, T, Nothing>()
    data class _3<T>(val value: T) : Choice3<Nothing, Nothing, T>()

    fun as1() = if (this is _1) value else null
    fun as2() = if (this is _2) value else null
    fun as3() = if (this is _3) value else null
}
