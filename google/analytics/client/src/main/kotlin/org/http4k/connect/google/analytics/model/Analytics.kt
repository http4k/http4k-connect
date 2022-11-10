package org.http4k.connect.google.analytics.model

sealed class Analytics {
    abstract val clientId: ClientId
    abstract val userAgent: String
}

data class Event(
    val category: String,
    val action: String = "",
    val label: String = "",
    val value: Int? = null,
    override val clientId: ClientId,
    override val userAgent: String = DEFAULT_USER_AGENT
) : Analytics()

data class PageView(
    val title: String,
    val path: String,
    val host: String,
    override val clientId: ClientId,
    override val userAgent: String = DEFAULT_USER_AGENT
) : Analytics()
