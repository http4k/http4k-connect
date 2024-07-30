package org.http4k.connect.amazon.systemsmanager

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import dev.forkhandles.result4k.failureOrNull
import org.http4k.connect.amazon.AwsContract
import org.http4k.connect.amazon.systemsmanager.model.ParameterType
import org.http4k.connect.amazon.systemsmanager.model.SSMParameterName
import org.http4k.connect.successValue
import org.http4k.core.HttpHandler
import org.http4k.core.Status.Companion.BAD_REQUEST
import org.junit.jupiter.api.Test
import java.util.UUID

abstract class SystemsManagerContract : AwsContract {
    private val secretsManager by lazy {
        SystemsManager.Http(aws.region, { aws.credentials }, http)
    }

    @Test
    fun `parameter lifecycle`() {
        val name = SSMParameterName.of(UUID.randomUUID().toString())
        assertThat(secretsManager.getParameter(name).failureOrNull()!!.status, equalTo(BAD_REQUEST))
        assertThat(secretsManager.putParameter(name, "value", ParameterType.String).successValue().Version, equalTo(1))
        assertThat(
            secretsManager.putParameter(name, "value", ParameterType.String).failureOrNull()!!.status,
            equalTo(BAD_REQUEST)
        )
        assertThat(
            secretsManager.putParameter(name, "value2", ParameterType.String, Overwrite = true).successValue().Version,
            equalTo(2)
        )
        assertThat(secretsManager.getParameter(name).successValue().Parameter.Value, equalTo("value2"))

        secretsManager.deleteParameter(name).successValue()

        assertThat(secretsManager.deleteParameter(name).failureOrNull()!!.status, equalTo(BAD_REQUEST))
    }
}
