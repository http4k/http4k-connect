import org.http4k.connect.amazon.model.ARN
import org.http4k.template.ViewModel

object DeleteTopicResponse : ViewModel
class CreateTopicResponse(of: ARN) : ViewModel

