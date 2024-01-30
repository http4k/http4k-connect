package org.http4k.connect.amazon.lambda

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.http4k.connect.amazon.AwsContract
import org.http4k.connect.amazon.apprunner.AppRunner
import org.http4k.connect.amazon.apprunner.Http
import org.http4k.connect.amazon.apprunner.action.SourceConfiguration
import org.http4k.connect.amazon.apprunner.createService
import org.http4k.connect.amazon.apprunner.deleteService
import org.http4k.connect.amazon.apprunner.listServices
import org.http4k.connect.amazon.apprunner.model.ServiceName
import org.http4k.connect.successValue
import org.http4k.core.HttpHandler
import org.http4k.filter.debug
import org.junit.jupiter.api.Test

abstract class AppRunnerContract(private val http: HttpHandler) : AwsContract() {

    private val appRunner by lazy {
        AppRunner.Http(aws.region, { aws.credentials }, http)
    }

    @Test
    fun `service lifecycle`() {
        val arn =
            appRunner.createService(ServiceName.of("foobar"), SourceConfiguration()).successValue().Service.ServiceArn

        assertThat(
            appRunner.listServices().successValue().ServiceSummaryList.map { it.ServiceArn },
            equalTo(listOf(arn))
        )

        appRunner.deleteService(arn).successValue()
    }
}
