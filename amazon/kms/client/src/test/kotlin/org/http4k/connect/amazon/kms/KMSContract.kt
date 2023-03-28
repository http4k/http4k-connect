package org.http4k.connect.amazon.kms

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.endsWith
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.present
import dev.forkhandles.result4k.failureOrNull
import org.http4k.connect.amazon.AwsContract
import org.http4k.connect.amazon.core.model.toARN
import org.http4k.connect.amazon.kms.model.CustomerMasterKeySpec.RSA_2048
import org.http4k.connect.amazon.kms.model.CustomerMasterKeySpec.RSA_3072
import org.http4k.connect.amazon.kms.model.EncryptionAlgorithm.RSAES_OAEP_SHA_256
import org.http4k.connect.amazon.kms.model.KeyUsage.ENCRYPT_DECRYPT
import org.http4k.connect.amazon.kms.model.KeyUsage.SIGN_VERIFY
import org.http4k.connect.amazon.kms.model.SigningAlgorithm.RSASSA_PKCS1_V1_5_SHA_256
import org.http4k.connect.amazon.kms.model.SigningAlgorithm.RSASSA_PSS_SHA_256
import org.http4k.connect.model.Base64Blob
import org.http4k.connect.successValue
import org.http4k.core.HttpHandler
import org.http4k.core.Status.Companion.BAD_REQUEST
import org.junit.jupiter.api.Test
import java.security.KeyFactory
import java.security.Signature
import java.security.spec.X509EncodedKeySpec


abstract class KMSContract(http: HttpHandler) : AwsContract() {
    private val kms by lazy {
        KMS.Http(aws.region, { aws.credentials }, http)
    }

    @Test
    fun `encrypt-decrypt key lifecycle`() {
        val plaintext = Base64Blob.encode("hello there")

        val creation = kms.createKey(RSA_3072, ENCRYPT_DECRYPT).successValue()

        val keyId = creation.KeyMetadata.KeyId
        assertThat(keyId, present())

        assertThat(kms.listKeys().successValue().Keys.any { it.KeyId == keyId }, equalTo(true))

        try {
            val describe = kms.describeKey(keyId).successValue()
            assertThat(describe.KeyMetadata.KeyId, equalTo(keyId))

            val encrypt = kms.encrypt(keyId, plaintext, RSAES_OAEP_SHA_256).successValue()
            assertThat(encrypt.KeyId.toARN().value, endsWith(keyId.value))

            val decrypt = kms.decrypt(keyId, encrypt.CiphertextBlob, RSAES_OAEP_SHA_256).successValue()
            assertThat(decrypt.KeyId.toARN().value, endsWith(keyId.value))
            assertThat(decrypt.Plaintext, equalTo(plaintext))

            val publicKey = kms.getPublicKey(keyId).successValue()
            assertThat(publicKey.KeyId.toARN().value, endsWith(keyId.value))
        } finally {
            val deletion = kms.scheduleKeyDeletion(keyId, 7).successValue()
            assertThat(deletion.KeyId.toARN().value, endsWith(keyId.value))
        }
    }

    @Test
    fun `sign-verify key lifecycle`() {
        val message1 = Base64Blob.encode("hello there")
        val message2 = Base64Blob.encode("goodbye there")

        val creation = kms.createKey(RSA_3072, SIGN_VERIFY).successValue()
        val keyId = creation.KeyMetadata.KeyId
        assertThat(keyId, present())

        try {
            val describe = kms.describeKey(keyId).successValue()
            assertThat(describe.KeyMetadata.KeyId, equalTo(keyId))

            val signed = kms.sign(keyId, message1, RSASSA_PSS_SHA_256).successValue()
            assertThat(signed.SigningAlgorithm, equalTo(RSASSA_PSS_SHA_256))

            val verification = kms.verify(keyId, message1, signed.Signature, RSASSA_PSS_SHA_256).successValue()
            assertThat(verification.SignatureValid, equalTo(true))

            val verificationFailure =
                kms.verify(keyId, message2, signed.Signature, RSASSA_PSS_SHA_256).failureOrNull()
            assertThat(verificationFailure!!.status, equalTo(BAD_REQUEST))
        } finally {
            val deletion = kms.scheduleKeyDeletion(keyId, 7).successValue()
            assertThat(deletion.KeyId.toARN().value, endsWith(keyId.value))
        }
    }

    @Test
    fun `retrieve key for signing`() {
        val message1 = Base64Blob.encode("hello there")

        val creation = kms.createKey(RSA_2048, SIGN_VERIFY).successValue()
        val keyId = creation.KeyMetadata.KeyId
        assertThat(keyId, present())

        try {
            val signed = kms.sign(keyId, message1, RSASSA_PKCS1_V1_5_SHA_256).successValue()

            assertThat(signed.SigningAlgorithm, equalTo(RSASSA_PKCS1_V1_5_SHA_256))

            val publicKey = kms.getPublicKey(keyId).successValue().PublicKey

            val keySpec = X509EncodedKeySpec(publicKey.decodedBytes())

            assertThat(
                Signature.getInstance("SHA256withRSA").apply {
                    initVerify(KeyFactory.getInstance("RSA").generatePublic(keySpec))
                    update(message1.decodedBytes())
                }.verify(signed.Signature.decodedBytes()), equalTo(true)
            )

        } finally {
            val deletion = kms.scheduleKeyDeletion(keyId, 7).successValue()
            assertThat(deletion.KeyId.toARN().value, endsWith(keyId.value))
        }
    }
}
