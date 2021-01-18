import org.http4k.connect.amazon.model.ARN
import org.http4k.connect.amazon.model.SNSMessageId
import org.http4k.template.ViewModel

object DeleteTopicResponse : ViewModel
class CreateTopicResponse(topicArn: ARN) : ViewModel
class ListTopicsResponse(arns: List<ARN>) : ViewModel
class PublishResponse(messageId: SNSMessageId) : ViewModel

