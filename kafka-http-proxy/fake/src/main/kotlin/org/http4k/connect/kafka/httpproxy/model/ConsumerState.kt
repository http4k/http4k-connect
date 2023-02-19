package org.http4k.connect.kafka.httpproxy.model

data class ConsumerState(
    val instances: Set<ConsumerInstanceId>,
    val autoCommitEnable: AutoCommitEnable,
    val offsets: Map<Topic, TopicCommitState>
) {
    fun committedRecords(topic: Topic) = offsets[topic]?.committed?.value ?: 0

    fun add(instance: ConsumerInstanceId) = copy(instances = instances + instance)
    fun remove(instance: ConsumerInstanceId) = copy(instances = instances - instance)

    fun new(topic: Topic) =
        copy(
            offsets =
            offsets + (topic to offsets.getOrDefault(topic, TopicCommitState()))
        )

    fun next(topic: Topic, lastOffset: Offset) =
        copy(
            offsets =
            offsets + (topic to offsets.getOrDefault(topic, TopicCommitState()).next(lastOffset))
        )

    fun commitAt(topic: Topic, lastOffset: Offset) =
        copy(
            offsets =
            offsets + (topic to offsets.getOrDefault(topic, TopicCommitState()).commitAt(lastOffset))
        )
}
