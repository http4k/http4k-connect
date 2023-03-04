package org.http4k.connect.kafka.rest

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import http4k.RandomKey.`SCHEMA$`
import org.http4k.connect.kafka.schemaregistry.Http
import org.http4k.connect.kafka.schemaregistry.SchemaRegistry
import org.http4k.connect.kafka.schemaregistry.checkSchemaRegistered
import org.http4k.connect.kafka.schemaregistry.model.SchemaType.AVRO
import org.http4k.connect.kafka.schemaregistry.model.Subject
import org.http4k.connect.kafka.schemaregistry.registerSchemaVersion
import org.http4k.connect.successValue
import org.http4k.core.Credentials
import org.http4k.core.HttpHandler
import org.http4k.core.Method.GET
import org.http4k.core.Request
import org.http4k.core.Status.Companion.OK
import org.http4k.core.Uri
import org.http4k.filter.debug
import org.junit.jupiter.api.Assumptions.assumeTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.UUID

abstract class SchemaRegistryContract {

    abstract val http: HttpHandler
    abstract val uri: Uri

    val subject = Subject.of(UUID.randomUUID().toString())

    private val schemaRegistry by lazy {
        SchemaRegistry.Http(Credentials("", ""), uri, http.debug())
    }

    @BeforeEach
    fun `can get to proxy`() {
        assumeTrue(http(Request(GET, uri)).status == OK)
    }

    @Test
    fun `schema lifecycle`() {
        with(schemaRegistry) {
            assertThat(checkSchemaRegistered(subject, `SCHEMA$`).successValue(), equalTo(null))

            registerSchemaVersion(
                subject,
                `SCHEMA$`,
                AVRO,
                listOf()
            ).successValue()

            assertThat(
                checkSchemaRegistered(subject, `SCHEMA$`).successValue()?.schema,
                equalTo(`SCHEMA$`)
            )
        }
    }

}
