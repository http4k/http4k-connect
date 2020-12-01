package org.http4k.connect.amazon.sts

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.http4k.connect.amazon.AwsContract
import org.http4k.connect.amazon.model.ARN
import org.http4k.connect.amazon.model.AwsService
import org.http4k.connect.successValue
import org.http4k.core.HttpHandler
import org.http4k.filter.debug
import org.junit.jupiter.api.Test
import java.util.UUID

abstract class STSContract(http: HttpHandler): AwsContract(AwsService.of("sts"), http) {

    private val sts by lazy {
        STS.Http(aws.scope, { aws.credentials }, http.debug())
    }

    @Test
    fun `assume role`() {
        val result = sts.assumeRole(AssumeRole.Request(
            ARN.of("arn:aws:iam::169766454405:role/TESTROLE"),
            UUID.randomUUID().toString()
        ))

        assertThat(result.successValue()
            .AssumeRoleResponse.AssumeRoleResult
            .Credentials.AccessKeyId, equalTo("accessKeyId"))
    }
}
