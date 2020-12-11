package org.http4k.connect.amazon.systemsmanager

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import dev.forkhandles.result4k.failureOrNull
import org.http4k.connect.amazon.AwsContract
import org.http4k.connect.amazon.model.AwsService
import org.http4k.connect.amazon.model.ParameterType
import org.http4k.connect.successValue
import org.http4k.core.HttpHandler
import org.http4k.core.Status.Companion.BAD_REQUEST
import org.junit.jupiter.api.Test
import java.util.UUID

abstract class SystemsManagerContract(http: HttpHandler) : AwsContract(AwsService.of("ssm"), http) {
    private val secretsManager by lazy {
        SystemsManager.Http(aws.scope, { aws.credentials }, http)
    }

    @Test
    fun `parameter lifecycle`() {
        val name = UUID.randomUUID().toString()
        with(secretsManager) {
            assertThat(get(GetParameterRequest(name)).failureOrNull()!!.status, equalTo(BAD_REQUEST))
            assertThat(put(PutParameterRequest(name, "value", ParameterType.String)).successValue().Version, equalTo(1))
            assertThat(put(PutParameterRequest(name, "value", ParameterType.String)).failureOrNull()!!.status, equalTo(BAD_REQUEST))
            assertThat(get(GetParameterRequest(name)).successValue().Parameter.Value, equalTo("value"))

            delete(DeleteParameterRequest(name)).successValue()

            assertThat(delete(DeleteParameterRequest(name)).failureOrNull()!!.status, equalTo(BAD_REQUEST))
        }
    }

}
