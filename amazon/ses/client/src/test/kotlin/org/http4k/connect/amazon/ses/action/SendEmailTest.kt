@file:Suppress("TestFunctionName")

package org.http4k.connect.amazon.ses.action

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.http4k.connect.amazon.ses.action.SendEmailTest.EmailMessageType.HTML
import org.http4k.connect.amazon.ses.action.SendEmailTest.EmailMessageType.TEXT
import org.http4k.connect.amazon.ses.action.SendEmailTest.RecipientType.BCC
import org.http4k.connect.amazon.ses.action.SendEmailTest.RecipientType.CC
import org.http4k.connect.amazon.ses.action.SendEmailTest.RecipientType.TO
import org.http4k.connect.amazon.ses.model.Destination
import org.http4k.connect.amazon.ses.model.EmailAddress
import org.http4k.connect.amazon.ses.model.HtmlMessage
import org.http4k.connect.amazon.ses.model.Message
import org.http4k.connect.amazon.ses.model.Subject
import org.http4k.connect.amazon.ses.model.TextMessage
import org.http4k.core.body.form
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource

class SendEmailTest {

    enum class EmailMessageType {
        TEXT, HTML
    }

    @ParameterizedTest
    @EnumSource(EmailMessageType::class)
    fun `creates html or text email message`(messageType: EmailMessageType) {
        val request = SendEmail().copy(
            message = Message(
                subject = Subject.of("My Subject"),
                body = when (messageType) {
                    TEXT -> TextMessage.of("My Text Message")
                    HTML -> HtmlMessage.of("My HTML Message")
                }
            )
        ).toRequest()

        assertThat(
            request.form().map { it.first }.toSet(), equalTo(
                setOf(
                    "Action",
                    "Source",
                    "Destination.ToAddresses.member.1",
                    "ReplyToAddresses.member.1",
                    "Message.Subject.Data",
                    when (messageType) {
                        TEXT -> "Message.Body.Text.Data"
                        HTML -> "Message.Body.Html.Data"
                    }
                )
            )
        )

        assertThat(request.form("Action"), equalTo("SendEmail"))
        assertThat(request.form("Source"), equalTo("source@example.com"))
        assertThat(request.form("Destination.ToAddresses.member.1"), equalTo("destination@example.com"))
        assertThat(request.form("Message.Subject.Data"), equalTo("My Subject"))
        assertThat(request.form("ReplyToAddresses.member.1"), equalTo("reply@example.com"))
        when (messageType) {
            TEXT -> assertThat(request.form("Message.Body.Text.Data"), equalTo("My Text Message"))
            HTML -> assertThat(request.form("Message.Body.Html.Data"), equalTo("My HTML Message"))
        }
    }

    enum class RecipientType {
        TO, CC, BCC
    }

    @ParameterizedTest
    @EnumSource(RecipientType::class)
    fun `creates email message with multiple recipients`(recipientType: RecipientType) {
        val numberOfRecipients = 1..10
        val addresses = numberOfRecipients.map { EmailAddress.of("destination$it@example.com") }.toSet()
        val destination = when (recipientType) {
            TO -> Destination(toAddresses = addresses)
            CC -> Destination(ccAddresses = addresses)
            BCC -> Destination(bccAddresses = addresses)
        }

        val request = SendEmail().copy(destination = destination).toRequest()

        numberOfRecipients.forEach {
            val nameFn = when (recipientType) {
                TO -> { idx: Int -> "Destination.ToAddresses.member.$idx" }
                CC -> { idx: Int -> "Destination.CcAddresses.member.$idx" }
                BCC -> { idx: Int -> "Destination.BccAddresses.member.$idx" }
            }
            assertThat(request.form(nameFn(it)), equalTo("destination$it@example.com"))
        }
    }

    private fun SendEmail() = SendEmail(
        source = EmailAddress.of("source@example.com"),
        destination = Destination(
            toAddresses = setOf(EmailAddress.of("destination@example.com"))
        ),
        message = Message(
            subject = Subject.of("My Subject"),
            body = TextMessage.of("My Text Message")
        ),
        replyToAddresses = setOf(EmailAddress.of("reply@example.com"))
    )
}
