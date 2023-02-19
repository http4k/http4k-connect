package org.http4k.connect.kafka.httpproxy.model

data class CommitState(
    val instances: Set<ConsumerInstanceId>,
    val autoCommitEnable: AutoCommitEnable,
    val offsets: Map<Topic, TopicCommitState>
) {
    fun add(instance: ConsumerInstanceId) = copy(instances = instances + instance)
    fun remove(instance: ConsumerInstanceId) = copy(instances = instances - instance)

    fun new(topic: Topic) =
        copy(
            offsets =
            offsets + (topic to offsets.getOrDefault(topic, TopicCommitState()))
        )

    fun next(topic: Topic, last: Offset) =
        copy(
            offsets =
            offsets + (topic to offsets.getOrDefault(topic, TopicCommitState()).next(last))
        )

    fun committed(topic: Topic, new: Offset) =
        copy(
            offsets =
            offsets + (topic to offsets.getOrDefault(topic, TopicCommitState()).committed(new))
        )
}
