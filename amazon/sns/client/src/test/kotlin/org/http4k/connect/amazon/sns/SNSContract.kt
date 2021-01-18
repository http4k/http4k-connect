package org.http4k.connect.amazon.sns

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.http4k.connect.amazon.AwsContract
import org.http4k.connect.amazon.model.Base64Blob
import org.http4k.connect.amazon.model.DataType.Number
import org.http4k.connect.amazon.model.Tag
import org.http4k.connect.amazon.model.TopicName
import org.http4k.connect.amazon.sns.action.MessageAttribute
import org.http4k.connect.successValue
import org.http4k.core.HttpHandler
import org.junit.jupiter.api.Test
import java.util.UUID

abstract class SNSContract(http: HttpHandler) : AwsContract(http) {
    private val sns by lazy {
        SNS.Http(aws.region, { aws.credentials }, http)
    }

    private val topicName = TopicName.of(UUID.randomUUID().toString())

    @Test
    fun `topic lifecycle`() {
        with(sns) {
            val topicArn = createTopic(
                topicName,
                listOf(Tag("key", "value"), Tag("key2", "value2")),
                mapOf("foo" to "bar")
            ).successValue().topicArn
            try {
                assertThat(listTopics().successValue().contains(topicArn), equalTo(true))

                publishMessage(
                    "hello world", "subject", topicArn = topicArn,
                    attributes = listOf(
                        MessageAttribute("foo", "123", Number),
                        MessageAttribute("bar", "123", Number),
                        MessageAttribute("binaryfoo", Base64Blob.encoded("foobar"))
                    )
                ).successValue()
            } finally {
                deleteTopic(topicArn).successValue()
            }
        }
    }
}
//
//Action=Publish
//Version=2010-03-31
//MessageAttribute.1.Name=foo
//MessageAttribute.1.Value.DataType=Number
//MessageAttribute.1.Value.StringValue=123
//Message=hello+world
//TopicArn=arn%3Aaws%3Asns%3Aeu-central-1%3A169766454405%3Aefc1ca3d-7b86-4088-afc5-99ab05db4c61
//
//Action=Publish
//Version=2010-03-31
//TopicArn=arn%3Aaws%3Asns%3Aeu-central-1%3A169766454405%3Aefc1ca3d-7b86-4088-afc5-99ab05db4c61
//Message=helllo
//MessageAttributes.entry.1.Name=foo
//MessageAttributes.entry.1.Value.DataType=String
//MessageAttributes.entry.1.Value.StringValue=asd


//Action=Publish
//Version=2010-03-31
//MessageAttributes.entry.1.Name=foo
//MessageAttributes.entry.1.Value.DataType=Number
//MessageAttributes.1.Value.StringValue=123
//MessageAttributes.entry.2.Name=bar
//MessageAttributes.entry.2.Value.DataType=Number
//MessageAttributes.2.Value.StringValue=123
//MessageAttributes.entry.3.Name=binaryfoo
//MessageAttributes.entry.3.Value.DataType=Binary
//MessageAttributes.3.Value.BinaryValue=Zm9vYmFy
//Message=hello+world
//TopicArn=arn%3Aaws%3Asns%3Aeu-central-1%3A169766454405%3Ad94fe297-4be8-4433-9736-4622ce31c4be
