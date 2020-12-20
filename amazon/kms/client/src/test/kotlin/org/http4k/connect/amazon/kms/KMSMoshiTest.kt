package org.http4k.connect.amazon.kms

import org.http4k.connect.SystemMoshiContract
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
import org.http4k.connect.amazon.kms.action.PublicKey
import org.http4k.connect.amazon.kms.action.ScheduleKeyDeletion
import org.http4k.connect.amazon.kms.action.Sign
import org.http4k.connect.amazon.kms.action.Signed
import org.http4k.connect.amazon.kms.action.Verify
import org.http4k.connect.amazon.kms.action.VerifyResult
import org.http4k.connect.amazon.model.ARN
import org.http4k.connect.amazon.model.AwsAccount
import org.http4k.connect.amazon.model.Base64Blob
import org.http4k.connect.amazon.model.CustomerMasterKeySpec.SYMMETRIC_DEFAULT
import org.http4k.connect.amazon.model.EncryptionAlgorithm.RSAES_OAEP_SHA_256
import org.http4k.connect.amazon.model.KMSKeyId
import org.http4k.connect.amazon.model.KeyMetadata
import org.http4k.connect.amazon.model.KeyUsage.ENCRYPT_DECRYPT
import org.http4k.connect.amazon.model.SigningAlgorithm.RSASSA_PKCS1_V1_5_SHA_384
import org.http4k.connect.amazon.model.SigningAlgorithm.RSASSA_PSS_SHA_256
import org.http4k.connect.amazon.model.Tag
import org.http4k.connect.amazon.model.Timestamp
import org.http4k.connect.randomString

val Blob = Base64Blob.encoded(randomString)

val KeyId = KMSKeyId.of(randomString)

val GrantTokens = listOf(randomString)

val EncryptionContext = mapOf(randomString to randomString)

val KeyUsage = ENCRYPT_DECRYPT
val CreationDate = Timestamp.of(0)
val KeyMetadata = KeyMetadata(KeyId, ARN.of(randomString), AwsAccount.of(randomString), KeyUsage,
    listOf(RSAES_OAEP_SHA_256), listOf(RSASSA_PSS_SHA_256), SYMMETRIC_DEFAULT, true, CreationDate)

class KMSMoshiTest : SystemMoshiContract(KMSMoshi,
    CreateKey(SYMMETRIC_DEFAULT, KeyUsage, true, randomString, randomString, randomString, randomString, listOf(Tag(randomString, randomString))),
    KeyCreated(KeyMetadata),
    Decrypt(KeyId, Blob, RSAES_OAEP_SHA_256, EncryptionContext, GrantTokens),
    Decrypted(KeyId, Blob, RSAES_OAEP_SHA_256),
    DescribeKey(KeyId, GrantTokens),
    KeyDescription(KeyMetadata),
    Encrypt(KeyId, Blob, RSAES_OAEP_SHA_256, EncryptionContext, GrantTokens),
    Encrypted(KeyId, Blob, RSAES_OAEP_SHA_256),
    GetPublicKey(KeyId, GrantTokens),
    PublicKey(KeyId, SYMMETRIC_DEFAULT, listOf(RSAES_OAEP_SHA_256), KeyUsage, Blob, listOf(RSASSA_PSS_SHA_256)),
    ScheduleKeyDeletion(KeyId, 123),
    KeyDeletionSchedule(KeyId, CreationDate),
    Sign(KeyId, Blob, RSASSA_PKCS1_V1_5_SHA_384, listOf(randomString), randomString),
    Signed(KeyId, Blob, RSASSA_PSS_SHA_256),
    Verify(KeyId, Blob, Blob, RSASSA_PSS_SHA_256, randomString, GrantTokens),
    VerifyResult(KeyId, true, RSASSA_PSS_SHA_256)
)
