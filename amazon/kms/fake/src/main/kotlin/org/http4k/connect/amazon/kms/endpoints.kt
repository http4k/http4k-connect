package org.http4k.connect.amazon.kms

import org.http4k.connect.amazon.AmazonJsonFake
import org.http4k.connect.amazon.core.model.ARN
import org.http4k.connect.amazon.core.model.AwsAccount
import org.http4k.connect.amazon.core.model.AwsService
import org.http4k.connect.amazon.core.model.KMSKeyId
import org.http4k.connect.amazon.core.model.Region
import org.http4k.connect.amazon.core.model.Timestamp
import org.http4k.connect.amazon.kms.KMSSigningAlgorithm.Companion.KMS_ALGORITHMS
import org.http4k.connect.amazon.kms.action.CreateKey
import org.http4k.connect.amazon.kms.action.Decrypt
import org.http4k.connect.amazon.kms.action.Decrypted
import org.http4k.connect.amazon.kms.action.DescribeKey
import org.http4k.connect.amazon.kms.action.Encrypt
import org.http4k.connect.amazon.kms.action.Encrypted
import org.http4k.connect.amazon.kms.action.GetPublicKey
import org.http4k.connect.amazon.kms.action.KeyCreated
import org.http4k.connect.amazon.kms.action.KeyDeletionSchedule
import org.http4k.connect.amazon.kms.action.KeyDescription
import org.http4k.connect.amazon.kms.action.KeyList
import org.http4k.connect.amazon.kms.action.ListKeys
import org.http4k.connect.amazon.kms.action.PublicKey
import org.http4k.connect.amazon.kms.action.ScheduleKeyDeletion
import org.http4k.connect.amazon.kms.action.Sign
import org.http4k.connect.amazon.kms.action.Signed
import org.http4k.connect.amazon.kms.action.Verify
import org.http4k.connect.amazon.kms.action.VerifyResult
import org.http4k.connect.amazon.kms.model.CustomerMasterKeySpec.SYMMETRIC_DEFAULT
import org.http4k.connect.amazon.kms.model.EncryptionAlgorithm
import org.http4k.connect.amazon.kms.model.KeyEntry
import org.http4k.connect.amazon.kms.model.KeyMetadata
import org.http4k.connect.amazon.kms.model.KeyUsage
import org.http4k.connect.amazon.kms.model.SigningAlgorithm
import org.http4k.connect.amazon.kms.model.SigningAlgorithm.RSASSA_PKCS1_V1_5_SHA_256
import org.http4k.connect.amazon.kms.model.SigningAlgorithm.RSASSA_PKCS1_V1_5_SHA_384
import org.http4k.connect.amazon.kms.model.SigningAlgorithm.RSASSA_PKCS1_V1_5_SHA_512
import org.http4k.connect.amazon.kms.model.SigningAlgorithm.RSASSA_PSS_SHA_256
import org.http4k.connect.amazon.kms.model.SigningAlgorithm.RSASSA_PSS_SHA_384
import org.http4k.connect.amazon.kms.model.SigningAlgorithm.RSASSA_PSS_SHA_512
import org.http4k.connect.model.Base64Blob
import org.http4k.connect.storage.Storage
import org.http4k.core.Response
import org.http4k.core.Status.Companion.BAD_REQUEST
import org.http4k.core.Status.Companion.OK
import java.security.KeyFactory
import java.security.spec.X509EncodedKeySpec
import java.util.Base64
import java.util.UUID


fun AmazonJsonFake.createKey(keys: Storage<StoredCMK>) = route<CreateKey> {
    val keyId = KMSKeyId.of(UUID.randomUUID().toString())
    val storedCMK = StoredCMK(
        keyId, keyId.toArn(), it.KeyUsage ?: KeyUsage.ENCRYPT_DECRYPT, it.CustomerMasterKeySpec
            ?: SYMMETRIC_DEFAULT
    )

    keys[storedCMK.arn.value] = storedCMK

    KeyCreated(KeyMetadata(storedCMK.keyId, storedCMK.arn, AwsAccount.of("0"), it.KeyUsage))
}

fun AmazonJsonFake.listKeys(keys: Storage<StoredCMK>) = route<ListKeys> {
    KeyList(
        keys.keySet("")
            .mapNotNull { keys[it] }
            .map { KeyEntry(it.keyId, it.arn) }
    )
}

fun AmazonJsonFake.describeKey(keys: Storage<StoredCMK>) = route<DescribeKey> { req ->
    keys[req.KeyId.toArn().value]?.let {
        KeyDescription(KeyMetadata(it.keyId, it.arn, AwsAccount.of("0"), it.keyUsage))
    }
}

fun AmazonJsonFake.decrypt(keys: Storage<StoredCMK>) = route<Decrypt> { req ->
    keys[req.KeyId.toArn().value]?.let {
        val plainText = Base64Blob.encode(req.CiphertextBlob.decoded().reversed())
        Decrypted(KMSKeyId.of(it.arn), plainText, req.EncryptionAlgorithm ?: EncryptionAlgorithm.SYMMETRIC_DEFAULT)
    }
}

fun AmazonJsonFake.encrypt(keys: Storage<StoredCMK>) = route<Encrypt> { req ->
    keys[req.KeyId.toArn().value]?.let {
        Encrypted(
            KMSKeyId.of(it.arn), Base64Blob.encode(req.Plaintext.decoded().reversed()), req.EncryptionAlgorithm
                ?: EncryptionAlgorithm.SYMMETRIC_DEFAULT
        )
    }
}

fun AmazonJsonFake.getPublicKey(keys: Storage<StoredCMK>, publicKey: Base64Blob) = route<GetPublicKey> {
    keys[it.KeyId.toArn().value]?.let {
        val derPublicKey = Base64Blob.of(
            Base64.getEncoder().encodeToString(KeyFactory.getInstance("RSA").generatePublic(
                X509EncodedKeySpec(Base64.getDecoder().decode(
                    publicKey.decoded()
                        .replace("-----BEGIN PUBLIC KEY-----", "")
                        .replace("-----END PUBLIC KEY-----", "")
                        .replace("\n", "")
                        .replace("\r", "")
                ))
            ).encoded)
        )
        PublicKey(
            KMSKeyId.of(it.arn), it.customerMasterKeySpec, it.keyUsage,
            derPublicKey, null,
            listOf(
                RSASSA_PKCS1_V1_5_SHA_256,
                RSASSA_PKCS1_V1_5_SHA_384,
                RSASSA_PKCS1_V1_5_SHA_512,
                RSASSA_PSS_SHA_256,
                RSASSA_PSS_SHA_384,
                RSASSA_PSS_SHA_512
            )
        )


    }
}

fun AmazonJsonFake.scheduleKeyDeletion(keys: Storage<StoredCMK>) = route<ScheduleKeyDeletion> { req ->
    keys[req.KeyId.toArn().value]?.let {
        keys[req.KeyId.toArn().value] = it.copy(deletion = Timestamp.of(Long.MAX_VALUE))
        KeyDeletionSchedule(KMSKeyId.of(it.arn), Timestamp.of(Long.MAX_VALUE))
    }
}

fun AmazonJsonFake.sign(keys: Storage<StoredCMK>, keyPairs: Map<SigningAlgorithm, KeyPair>) = route<Sign> { req ->
    keys[req.KeyId.toArn().value]?.let {
        Signed(
            KMSKeyId.of(it.arn),
            signTheBytes(req, keyPairs), req.SigningAlgorithm
        )
    }
}

private fun signTheBytes(req: Sign, keyPairs: Map<SigningAlgorithm, KeyPair>) =
    KMS_ALGORITHMS[req.SigningAlgorithm]?.sign(keyPairs[req.SigningAlgorithm]!!, req.Message)
        ?: Base64Blob.encode(req.SigningAlgorithm.name + req.Message.decoded().take(50))

fun AmazonJsonFake.verify(keys: Storage<StoredCMK>, keyPairs: Map<SigningAlgorithm, KeyPair>) =
    route<Verify>(
        {
            when ((it as? VerifyResult)?.SignatureValid) {
                true -> Response(OK).body(autoMarshalling.asFormatString(it))
                else -> Response(BAD_REQUEST).body("""{"__type":"KMSInvalidSignatureException"}""")
            }
        }
    ) { req ->
        keys[req.KeyId.toArn().value]?.let {
            VerifyResult(req.KeyId, verifyTheBytes(req, keyPairs), req.SigningAlgorithm)
        }
    }

private fun verifyTheBytes(
    req: Verify, keyPairs: Map<SigningAlgorithm, KeyPair>
) = KMS_ALGORITHMS[req.SigningAlgorithm]?.verify(keyPairs[req.SigningAlgorithm]!!, req.Message, req.Signature)
    ?: req.Signature.decoded().startsWith(req.SigningAlgorithm.name)

fun KMSKeyId.toArn() = when {
    value.startsWith("arn") -> ARN.of(value)
    else -> ARN.of(AwsService.of("kms"), Region.of("ldn-north-1"), AwsAccount.of("0"), "key", this)
}
