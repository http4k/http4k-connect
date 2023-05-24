package org.http4k.connect.openai.auth

import org.http4k.core.Credentials
import org.http4k.core.Filter
import org.http4k.filter.ServerFilters.BasicAuth
import org.http4k.filter.ServerFilters.BearerAuth
import org.http4k.security.AccessToken

/**
 * Plugin authentication models for Token validation
 */
interface PluginToken {
    val securityFilter: Filter
    val type: String

    class Basic(realm: String, check: (Credentials) -> Boolean) : PluginToken {
        override val type = "basic"

        override val securityFilter = BasicAuth(realm) { check(it) }
    }

    class Bearer(check: (AccessToken) -> Boolean) : PluginToken {
        override val type = "bearer"

        override val securityFilter = BearerAuth { check(AccessToken(it)) }
    }
}
