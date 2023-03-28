package org.http4k.connect.amazon.kms

import org.http4k.connect.amazon.kms.model.SigningAlgorithm
import org.http4k.connect.model.Base64Blob

fun Class<*>.loadKeyPairs(pair: Pair<SigningAlgorithm, KMSSigningAlgorithm>) = KeyPair(
    Base64Blob.encode(classLoader.getResource("${pair.first}.public")!!.readText()),
    Base64Blob.encode(classLoader.getResource("${pair.first}.private")!!.readText()),
    "password"
)
