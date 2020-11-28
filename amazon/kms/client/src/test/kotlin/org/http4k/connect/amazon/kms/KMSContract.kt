package org.http4k.connect.amazon.kms

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.endsWith
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.present
import dev.forkhandles.result4k.failureOrNull
import org.http4k.connect.amazon.AwsContract
import org.http4k.connect.amazon.model.AwsService
import org.http4k.connect.amazon.model.Base64Blob
import org.http4k.connect.amazon.model.SigningAlgorithm.ECDSA_SHA_256
import org.http4k.connect.amazon.model.toARN
import org.http4k.connect.successValue
import org.http4k.core.HttpHandler
import org.http4k.core.Status.Companion.BAD_REQUEST
import org.junit.jupiter.api.Test

abstract class KMSContract(http: HttpHandler) : AwsContract(AwsService.of("kms"), http) {
    private val kms by lazy {
        KMS.Http(aws.scope, { aws.credentials }, http)
    }

    @Test
    fun `signing key lifecycle`() {
        with(kms) {

            val plaintext = Base64Blob.encoded("hello there")

            val creation = create(CreateKey.Request()).successValue()
            val keyId = creation.KeyMetadata.KeyId
            assertThat(keyId, present())

            try {
                val describe = describe(DescribeKey.Request(keyId)).successValue()
                assertThat(describe.KeyMetadata.KeyId, equalTo(keyId))

                val encrypt = encrypt(Encrypt.Request(keyId, plaintext)).successValue()
                assertThat(encrypt.KeyId.toARN().value, endsWith(keyId.value))

                val decrypt = decrypt(Decrypt.Request(keyId, encrypt.CiphertextBlob)).successValue()
                assertThat(decrypt.KeyId.toARN().value, endsWith(keyId.value))
                assertThat(decrypt.Plaintext, equalTo(plaintext))

                val signed = sign(Sign.Request(keyId, Base64Blob.encoded("hello there"), ECDSA_SHA_256)).successValue()
                assertThat(signed.SigningAlgorithm, equalTo(ECDSA_SHA_256))

            } catch (e: AssertionError) {
                val deletion = scheduleDeletion(ScheduleKeyDeletion.Request(keyId)).successValue()
                assertThat(deletion.KeyId.toARN().value, endsWith(keyId.value))

                val failedDeletion = scheduleDeletion(ScheduleKeyDeletion.Request(keyId)).failureOrNull()
                assertThat(failedDeletion?.status, equalTo(BAD_REQUEST))
                throw e
            }

//            val signed = sign(Sign.Request(keyId, Base64Blob.encoded("hello there"), ECDSA_SHA_256)).successValue()
//            assertThat(signed.SigningAlgorithm, equalTo(ECDSA_SHA_256))
//
//            val verification = verify(Verify.Request(keyId, Base64Blob.encoded("hello there"), signed.Signature, ECDSA_SHA_256)).successValue()
//            assertThat(verification.SignatureValid, equalTo(true))

            // more tests...

        }
    }
}
