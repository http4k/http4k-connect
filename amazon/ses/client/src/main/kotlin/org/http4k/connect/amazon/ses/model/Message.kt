package org.http4k.connect.amazon.ses.model

data class Message(
    val subject: Subject,
    val html: Body,
    val text: Body? = null
)


