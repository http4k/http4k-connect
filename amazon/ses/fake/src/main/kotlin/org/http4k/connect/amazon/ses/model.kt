package org.http4k.connect.amazon.ses

import org.http4k.connect.amazon.ses.model.SESMessageId
import org.http4k.template.ViewModel

data class SendEmailResponse(val messageId: SESMessageId) : ViewModel
