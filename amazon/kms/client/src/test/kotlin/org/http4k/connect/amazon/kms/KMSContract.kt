package org.http4k.connect.amazon.kms

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.endsWith
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.present
import dev.forkhandles.result4k.failureOrNull
import org.http4k.connect.amazon.AwsContract
import org.http4k.connect.amazon.model.AwsService
import org.http4k.connect.amazon.model.Base64Blob
import org.http4k.connect.amazon.model.CustomerMasterKeySpec.RSA_3072
import org.http4k.connect.amazon.model.EncryptionAlgorithm.RSAES_OAEP_SHA_256
import org.http4k.connect.amazon.model.KeyUsage.ENCRYPT_DECRYPT
import org.http4k.connect.amazon.model.KeyUsage.SIGN_VERIFY
import org.http4k.connect.amazon.model.SigningAlgorithm.RSASSA_PKCS1_V1_5_SHA_384
import org.http4k.connect.amazon.model.SigningAlgorithm.RSASSA_PSS_SHA_256
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
    fun `encrypt-decrypt key lifecycle`() {
        val plaintext = Base64Blob.encoded("hello there")

        val creation = kms(CreateKey(RSA_3072, ENCRYPT_DECRYPT)).successValue()
        val keyId = creation.KeyMetadata.KeyId
        assertThat(keyId, present())

        val describe = kms(DescribeKey(keyId)).successValue()
        assertThat(describe.KeyMetadata.KeyId, equalTo(keyId))

        val encrypt = kms(Encrypt(keyId, plaintext, RSAES_OAEP_SHA_256)).successValue()
        assertThat(encrypt.KeyId.toARN().value, endsWith(keyId.value))

        val decrypt = kms(Decrypt(keyId, encrypt.CiphertextBlob, RSAES_OAEP_SHA_256)).successValue()
        assertThat(decrypt.KeyId.toARN().value, endsWith(keyId.value))
        assertThat(decrypt.Plaintext, equalTo(plaintext))

        val publicKey = kms(GetPublicKey(keyId)).successValue()
        assertThat(publicKey.KeyId.toARN().value, endsWith(keyId.value))

        val deletion = kms(ScheduleKeyDeletion(keyId)).successValue()
        assertThat(deletion.KeyId.toARN().value, endsWith(keyId.value))
    }

    @Test
    fun `sign-verify key lifecycle`() {
        val plaintext = Base64Blob.encoded("hello there")

        val creation = kms(CreateKey(RSA_3072, SIGN_VERIFY)).successValue()
        val keyId = creation.KeyMetadata.KeyId
        assertThat(keyId, present())

        val describe = kms(DescribeKey(keyId)).successValue()
        assertThat(describe.KeyMetadata.KeyId, equalTo(keyId))

        val signed = kms(Sign(keyId, plaintext, RSASSA_PSS_SHA_256)).successValue()
        assertThat(signed.SigningAlgorithm, equalTo(RSASSA_PSS_SHA_256))

        val verification = kms(Verify(keyId, plaintext, signed.Signature, RSASSA_PSS_SHA_256)).successValue()
        assertThat(verification.SignatureValid, equalTo(true))

        val verificationFailure = kms(Verify(keyId, plaintext, signed.Signature, RSASSA_PKCS1_V1_5_SHA_384)).failureOrNull()
        assertThat(verificationFailure!!.status, equalTo(BAD_REQUEST))

        val deletion = kms(ScheduleKeyDeletion(keyId)).successValue()
        assertThat(deletion.KeyId.toARN().value, endsWith(keyId.value))
    }
}
