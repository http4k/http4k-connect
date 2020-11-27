package org.http4k.connect.amazon.kms

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.http4k.connect.amazon.AwsContract
import org.http4k.core.HttpHandler
import org.junit.jupiter.api.Test

abstract class KMSContract(http: HttpHandler) : AwsContract(http) {
    private val kms by lazy {
        KMS.Http(aws.scope, { aws.credentials }, http)
    }

    @Test
    fun `key lifecycle`() {
        kms.createKey(CreateKey.Request())
        assertThat(false, equalTo(false))
    }

}
