package org.http4k.connect.amazon.eventbridge

import org.http4k.connect.amazon.AmazonJsonFake
import org.http4k.connect.amazon.core.model.ARN
import org.http4k.connect.amazon.core.model.AwsAccount
import org.http4k.connect.amazon.core.model.Region
import org.http4k.connect.amazon.eventbridge.action.CreateEventBus
import org.http4k.connect.amazon.eventbridge.action.CreatedEventBus
import org.http4k.connect.amazon.eventbridge.action.DeleteEventBus
import org.http4k.connect.amazon.eventbridge.action.EventBuses
import org.http4k.connect.amazon.eventbridge.action.EventResult
import org.http4k.connect.amazon.eventbridge.action.EventResults
import org.http4k.connect.amazon.eventbridge.action.ListEventBuses
import org.http4k.connect.amazon.eventbridge.action.PutEvents
import org.http4k.connect.amazon.eventbridge.model.Event
import org.http4k.connect.amazon.eventbridge.model.EventBus
import org.http4k.connect.amazon.model.EventBusName
import org.http4k.connect.amazon.model.EventId
import org.http4k.connect.storage.Storage
import java.util.UUID

fun AmazonJsonFake.putEvents(events: Storage<List<Event>>) = route<PutEvents> {
    val newEvents = it.Entries.groupBy {
        it.EventBusName ?: EventBusName.of("default")
    }

    EventResults(newEvents.flatMap { (bus, new) ->
        events[bus.value] = (events[bus.value] ?: listOf()) + new
        new.map {
            EventResult(
                EventId.of(UUID.nameUUIDFromBytes(it.toString().toByteArray()).toString()),
                null, null
            )
        }
    }, 0)
}

fun AmazonJsonFake.createEventBus(events: Storage<List<Event>>) = route<CreateEventBus> {
    events[it.Name.value] = listOf()
    CreatedEventBus(it.Name.toArn())
}

fun AmazonJsonFake.listEventBuses(events: Storage<List<Event>>) = route<ListEventBuses> {
    EventBuses(events.keySet().map {
        EventBus(
            EventBusName.of(it).toArn(),
            EventBusName.of(it),
            null
        )
    }, null)
}

fun AmazonJsonFake.deleteEventBus(records: Storage<List<Event>>) = route<DeleteEventBus> {
    records.remove(it.Name.value)
    Unit
}

private fun EventBusName.toArn() = ARN.of(
    EventBridge.awsService,
    Region.of("us-east-1"),
    AwsAccount.of("0"),
    "eventbus", this
)
