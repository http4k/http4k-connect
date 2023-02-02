package org.http4k.connect.amazon.sns

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.http4k.connect.amazon.core.model.Base64Blob
import org.http4k.connect.amazon.core.model.DataType.Number
import org.http4k.connect.amazon.fakeAwsEnvironment
import org.http4k.connect.amazon.sns.model.MessageAttribute
import org.http4k.connect.storage.InMemory
import org.http4k.connect.storage.Storage
import org.http4k.connect.successValue
import org.http4k.filter.debug
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test

val topics = Storage.InMemory<List<SNSMessage>>()

class FakeSNSTest : SNSContract(FakeSNS(topics).debug()) {
    override val aws = fakeAwsEnvironment

    @AfterEach
    fun clean() {
        topics.removeAll()
    }

    @Test
    fun `topic messages are parsed`() {
        with(sns) {
            val topicArn = createTopic(topicName, listOf(), mapOf()).successValue().topicArn
            try {
                val attributes = listOf(
                    MessageAttribute("foo", "123", Number),
                    MessageAttribute("bar", "123", Number),
                    MessageAttribute("binaryfoo", Base64Blob.encode("foobar"))
                )
                publishMessage(
                    "hello world", "subject", topicArn = topicArn,
                    attributes = attributes
                ).successValue()

                assertThat(
                    topics[topicName.value], equalTo(
                        listOf(
                            SNSMessage(
                                "hello world", "subject",
                                emptyList()
                            )
                        )
                    )
                )
            } finally {
                deleteTopic(topicArn).successValue()
            }
        }
    }

}
