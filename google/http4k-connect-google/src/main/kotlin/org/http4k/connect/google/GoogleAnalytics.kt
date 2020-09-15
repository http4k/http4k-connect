package org.http4k.connect.google

import org.http4k.core.Filter
import org.http4k.core.HttpHandler
import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.core.body.form
import org.http4k.routing.RoutedRequest
import java.util.UUID

interface GoogleAnalytics {
    fun pageView(clientId: String, documentTitle: String, documentPath: String, documentHost: String)

    companion object {
        fun Http(analyticsHandler: HttpHandler, trackingId: String) = object : GoogleAnalytics {
            override fun pageView(clientId: String, documentTitle: String, documentPath: String, documentHost: String) {
                analyticsHandler(Request(POST, "/collect")
                    .form(VERSION, "1")
                    .form(MEASUREMENT_ID, trackingId)
                    .form(CLIENT_ID, clientId)
                    .form(DOCUMENT_TITLE, documentTitle)
                    .form(DOCUMENT_PATH, documentPath)
                    .form(DOCUMENT_HOST, documentHost)
                )
            }
        }

        fun LogPageView(analytics: GoogleAnalytics, clientId: (Request) -> String = { UUID.randomUUID().toString() }): Filter = object : Filter {
            override fun invoke(handler: HttpHandler): HttpHandler = { request ->
                handler(request).also {
                    if (it.status.successful || it.status.informational || it.status.redirection) {
                        val host = request.header("host") ?: request.uri.host
                        val path = when (request) {
                            is RoutedRequest -> request.xUriTemplate.toString()
                            else -> request.uri.path
                        }
                        analytics.pageView(clientId(request), path, path, host)
                    }
                }
            }

        }

        const val VERSION = "v"
        const val MEASUREMENT_ID = "tid"
        const val CLIENT_ID = "cid"
        const val DOCUMENT_TITLE = "dt"
        const val DOCUMENT_PATH = "dp"
        const val DOCUMENT_HOST = "dh"
    }

}
