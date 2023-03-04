package org.http4k.connect.kafka.schemaregistry.endpoints

import org.apache.avro.Schema
import org.http4k.connect.kafka.schemaregistry.SchemaRegistryMoshi.auto
import org.http4k.connect.kafka.schemaregistry.action.PostedSchema
import org.http4k.connect.kafka.schemaregistry.action.SchemaId
import org.http4k.connect.storage.Storage
import org.http4k.core.Body
import org.http4k.core.ContentType
import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.SCHEMA_REGISTRY
import org.http4k.core.Status.Companion.CONFLICT
import org.http4k.core.Status.Companion.OK
import org.http4k.core.with
import org.http4k.lens.Path
import org.http4k.routing.bind
import kotlin.math.absoluteValue

fun registerSchema(schemas: Storage<Schema>) = "/subjects/{subject}/versions" bind POST to
    { req: Request ->
        val subject = Path.of("subject")(req)

        val posted = Body.auto<PostedSchema>().toLens()(req).schema
        val version = posted.toString().hashCode().absoluteValue
        when {
            schemas[subject + posted.toString().hashCode().absoluteValue] != null -> Response(CONFLICT)
            else -> {
                schemas[subject + version] = posted
                Response(OK)
                    .with(
                        Body.auto<SchemaId>(contentType = ContentType.SCHEMA_REGISTRY)
                            .toLens() of SchemaId(version)
                    )
            }
        }
    }
