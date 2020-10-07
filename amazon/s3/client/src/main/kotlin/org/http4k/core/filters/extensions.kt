package org.http4k.core.filters

import org.http4k.core.Filter
import org.http4k.filter.ClientFilters

/**
 * use http4k version when released..
 */
fun ClientFilters.SetXForwardedHost() = Filter { next ->
    {
        next(it.header("host")
            ?.let { host -> it.replaceHeader("X-Forwarded-Host", host) }
            ?: it
        )
    }
}
