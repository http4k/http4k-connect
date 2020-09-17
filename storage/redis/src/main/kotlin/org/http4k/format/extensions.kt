package org.http4k.format

import org.http4k.connect.storage.Redis
import org.http4k.connect.storage.Storage
import org.http4k.core.Uri

inline fun <reified T : Any> Storage.Companion.RedisJackson(uri: Uri) = Redis<T>(uri, Jackson)
