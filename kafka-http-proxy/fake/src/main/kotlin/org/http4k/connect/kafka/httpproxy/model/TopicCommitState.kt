package org.http4k.connect.kafka.httpproxy.model

import dev.forkhandles.values.ZERO

data class TopicCommitState(
    val next: Offset = Offset.ZERO,
    val committed: Offset = Offset.ZERO
) {
    fun next(last: Offset) = TopicCommitState(last.inc(), committed)
    fun committed(last: Offset) = TopicCommitState(last.inc(), last.inc())
}
