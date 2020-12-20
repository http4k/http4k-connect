package org.http4k.connect.amazon.kms

import org.http4k.connect.amazon.kms.action.CreateKey
import org.http4k.connect.amazon.model.CustomerMasterKeySpec.SYMMETRIC_DEFAULT
import org.http4k.connect.amazon.model.KeyUsage.ENCRYPT_DECRYPT
import org.http4k.connect.amazon.model.Tag

class KMSMoshiTest : SystemMoshiTest(KMSMoshi,
    CreateKey(SYMMETRIC_DEFAULT, ENCRYPT_DECRYPT, true, randomString, randomString, randomString, randomString, listOf(Tag("key", "value")))
)
