package org.http4k.connect.amazon.kms

import org.http4k.connect.SystemMoshiTest
import org.http4k.connect.amazon.kms.action.CreateKey
import org.http4k.connect.amazon.kms.action.Decrypt
import org.http4k.connect.amazon.kms.action.DescribeKey
import org.http4k.connect.amazon.kms.action.Encrypt
import org.http4k.connect.amazon.kms.action.GetPublicKey
import org.http4k.connect.amazon.kms.action.ScheduleKeyDeletion
import org.http4k.connect.amazon.kms.action.Sign
import org.http4k.connect.amazon.kms.action.Verify
import org.http4k.connect.amazon.model.Base64Blob
import org.http4k.connect.amazon.model.CustomerMasterKeySpec.SYMMETRIC_DEFAULT
import org.http4k.connect.amazon.model.EncryptionAlgorithm.RSAES_OAEP_SHA_256
import org.http4k.connect.amazon.model.KMSKeyId
import org.http4k.connect.amazon.model.KeyUsage.ENCRYPT_DECRYPT
import org.http4k.connect.amazon.model.SigningAlgorithm.RSASSA_PKCS1_V1_5_SHA_384
import org.http4k.connect.amazon.model.SigningAlgorithm.RSASSA_PSS_SHA_256
import org.http4k.connect.amazon.model.Tag
import org.http4k.connect.randomString

val Blob = Base64Blob.encoded(randomString)

val KeyId = KMSKeyId.of(randomString)

val GrantTokens = listOf(randomString)

val EncryptionContext = mapOf(randomString to randomString)

class KMSMoshiTest : SystemMoshiTest(KMSMoshi,
    CreateKey(SYMMETRIC_DEFAULT, ENCRYPT_DECRYPT, true, randomString, randomString, randomString, randomString, listOf(Tag(randomString, randomString))),
    Decrypt(KeyId, Blob, RSAES_OAEP_SHA_256, EncryptionContext, GrantTokens),
    DescribeKey(KeyId, GrantTokens),
    Encrypt(KeyId, Blob, RSAES_OAEP_SHA_256, EncryptionContext, GrantTokens),
    GetPublicKey(KeyId, GrantTokens),
    ScheduleKeyDeletion(KeyId, 123),
    Sign(KeyId, Blob, RSASSA_PKCS1_V1_5_SHA_384, listOf(randomString), randomString),
    Verify(KeyId, Blob, Blob, RSASSA_PSS_SHA_256, randomString, GrantTokens)
)
