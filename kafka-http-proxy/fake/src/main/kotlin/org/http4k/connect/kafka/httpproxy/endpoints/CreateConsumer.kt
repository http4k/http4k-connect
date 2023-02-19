package org.http4k.connect.kafka.httpproxy.endpoints

import org.http4k.connect.kafka.httpproxy.CommitState
import org.http4k.connect.kafka.httpproxy.KafkaHttpProxyMoshi.auto
import org.http4k.connect.kafka.httpproxy.action.NewConsumer
import org.http4k.connect.kafka.httpproxy.model.AutoCommitEnable.`true`
import org.http4k.connect.kafka.httpproxy.model.Consumer
import org.http4k.connect.kafka.httpproxy.model.ConsumerGroup
import org.http4k.connect.kafka.httpproxy.model.ConsumerInstanceId
import org.http4k.connect.storage.Storage
import org.http4k.connect.storage.get
import org.http4k.connect.storage.set
import org.http4k.core.Body
import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.CONFLICT
import org.http4k.core.Status.Companion.OK
import org.http4k.core.Uri
import org.http4k.core.extend
import org.http4k.core.with
import org.http4k.lens.Path
import org.http4k.lens.value
import org.http4k.routing.bind

fun createConsumer(consumers: Storage<CommitState>, baseUri: Uri) =
    "/consumers/{consumerGroup}" bind POST to { req: Request ->
        val consumerGroup = Path.value(ConsumerGroup).of("consumerGroup")(req)
        val consumer = Body.auto<Consumer>().toLens()(req)
        val id = ConsumerInstanceId.of("$consumerGroup${consumer.name}")
        when {
            consumers[id] == null -> {
                consumers[id] = CommitState(
                    consumer.enableAutocommit == `true`, mapOf()
                )
                Response(OK).with(
                    Body.auto<NewConsumer>().toLens() of
                        NewConsumer(
                            id,
                            baseUri.extend(Uri.of("/consumers/$consumerGroup/instances/$id"))
                        )
                )
            }

            else -> Response(CONFLICT)
        }
    }

