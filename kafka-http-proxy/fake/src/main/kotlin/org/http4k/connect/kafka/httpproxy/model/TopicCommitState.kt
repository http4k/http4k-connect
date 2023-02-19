package org.http4k.connect.kafka.httpproxy.model

import dev.forkhandles.values.ZERO

data class TopicCommitState(
    val next: Offset = Offset.ZERO,
    val committed: Offset = Offset.ZERO
) {
    fun next(lastOffset: Offset) = TopicCommitState(lastOffset.inc(), committed)
    fun commitAt(lastOffset: Offset) = TopicCommitState(lastOffset.inc(), lastOffset.inc())
}
