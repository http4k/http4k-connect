package org.http4k.connect.amazon.kms

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.http4k.connect.amazon.kms.KMSSigningAlgorithm.Companion.KMS_ALGORITHMS
import org.http4k.connect.amazon.kms.model.SigningAlgorithm
import org.http4k.connect.model.Base64Blob
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

class KMSSigningAlgorithmTest {

    @ParameterizedTest
    @MethodSource("algorithms")
    fun `can sign and verify`(algorithm: Pair<SigningAlgorithm, KMSSigningAlgorithm>) {
        val keypair = FakeKMS.ALGORITHMS[algorithm.first]!!
        val message = Base64Blob.encode("foobar")
        val signature = algorithm.second.sign(keypair, message)

        assertThat(algorithm.second.verify(keypair, message, signature), equalTo(true))

        val invalid = Base64Blob.encode(signature.decodedBytes().reversed().toByteArray())
        assertThat(algorithm.second.verify(keypair, message, invalid), equalTo(false))
    }

    companion object {
        @JvmStatic
        fun algorithms() = KMS_ALGORITHMS.toList()
    }
}
