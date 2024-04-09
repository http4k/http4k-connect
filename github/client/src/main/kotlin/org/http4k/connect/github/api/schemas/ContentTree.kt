package org.http4k.connect.github.api.schemas

import org.http4k.base64Decoded
import org.http4k.connect.github.api.Link
import org.http4k.core.Uri

data class ContentTree(val type: String,
                       val encoding: String,
                       val size: Int,
                       val name: String,
                       val path: String,
                       val sha: String,
                       val url: Uri,
                       val content: String,
                       val git_url: Uri,
                       val html_url: Uri,
                       val download_url: Uri,
                       val entries: Array<Entry>?,
                       val _links: Link) {

    val rawContent by lazy { content.replace("\n", "").base64Decoded() }
}
