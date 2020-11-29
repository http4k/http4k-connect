package org.http4k.core

import org.http4k.filter.DebuggingFilters

@Suppress("unused")
fun HttpHandler.debug() = DebuggingFilters.PrintRequestAndResponse().then(this)
