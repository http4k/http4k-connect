package org.http4k.connect.amazon.kms

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.present
import dev.forkhandles.result4k.failureOrNull
import org.http4k.connect.amazon.AwsContract
import org.http4k.connect.amazon.model.AwsService
import org.http4k.connect.successValue
import org.http4k.core.HttpHandler
import org.http4k.core.Status.Companion.BAD_REQUEST
import org.http4k.core.then
import org.http4k.filter.DebuggingFilters.PrintRequestAndResponse
import org.junit.jupiter.api.Test

abstract class KMSContract(http: HttpHandler) : AwsContract(AwsService.of("kms"), http) {
    private val kms by lazy {
        KMS.Http(aws.scope, { aws.credentials }, PrintRequestAndResponse().then(http))
    }

    @Test
    fun `signing key lifecycle`() {
        with(kms) {
            val creation = create(CreateKey.Request()).successValue()
            val keyId = creation.KeyMetadata.KeyId
            assertThat(keyId, present())

            val describe = describe(DescribeKey.Request(keyId)).successValue()
            assertThat(describe.KeyMetadata.KeyId, equalTo(keyId))

//            val signed = sign(Sign.Request(keyId, Base64Blob.encoded("hello there"), ECDSA_SHA_256)).successValue()
//            assertThat(signed.SigningAlgorithm, equalTo(ECDSA_SHA_256))
//
//            val verification = verify(Verify.Request(keyId, Base64Blob.encoded("hello there"), signed.Signature, ECDSA_SHA_256)).successValue()
//            assertThat(verification.SignatureValid, equalTo(true))

            // more tests...

            val failedDeletion = scheduleDeletion(ScheduleKeyDeletion.Request(keyId)).failureOrNull()
            assertThat(failedDeletion?.status, equalTo(BAD_REQUEST))

            val describeDeleted = describe(DescribeKey.Request(keyId)).failureOrNull()
            assertThat(describeDeleted?.status, equalTo(BAD_REQUEST))
        }
    }
}
