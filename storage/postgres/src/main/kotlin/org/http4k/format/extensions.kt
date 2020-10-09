package org.http4k.format

import org.http4k.connect.storage.Postgres
import org.http4k.connect.storage.Storage
import org.http4k.core.Credentials

inline fun <reified T : Any> Storage.Companion.PostgresJackson(name: String, credentials: Credentials, url: String) = Postgres<T>(url, name, credentials, Jackson)
