package org.http4k.connect.github.api.schemas

import org.http4k.connect.github.api.Link
import org.http4k.core.Uri

data class Entry(val type: String,
                 val size: Int,
                 val name: String,
                 val path: String,
                 val content: String,
                 val sha: String,
                 val url: Uri,
                 val git_url: Uri,
                 val html_url: Uri,
                 val download_url: Uri,
                 val _links: Link)
