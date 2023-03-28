package org.http4k.connect.amazon.kms

import org.http4k.aws.AwsCredentials
import org.http4k.chaos.ChaoticHttpHandler
import org.http4k.chaos.start
import org.http4k.connect.amazon.AmazonJsonFake
import org.http4k.connect.amazon.core.model.AwsService
import org.http4k.connect.amazon.core.model.Region
import org.http4k.connect.amazon.kms.KMSSigningAlgorithm.Companion.KMS_ALGORITHMS
import org.http4k.connect.amazon.kms.model.SigningAlgorithm.RSASSA_PKCS1_V1_5_SHA_256
import org.http4k.connect.storage.InMemory
import org.http4k.connect.storage.Storage
import org.http4k.core.Method.POST
import org.http4k.routing.bind
import org.http4k.routing.routes

class FakeKMS(keys: Storage<StoredCMK> = Storage.InMemory()) : ChaoticHttpHandler() {

    private val api = AmazonJsonFake(KMSMoshi, AwsService.of("TrentService"))

    override val app = routes(
        "/" bind POST to routes(
            api.createKey(keys),
            api.describeKey(keys),
            api.decrypt(keys),
            api.encrypt(keys),
            api.getPublicKey(keys, ALGORITHMS[RSASSA_PKCS1_V1_5_SHA_256]!!.public),
            api.listKeys(keys),
            api.scheduleKeyDeletion(keys),
            api.sign(keys, ALGORITHMS),
            api.verify(keys, ALGORITHMS)
        )
    )

    companion object {
        internal val ALGORITHMS by lazy {
            KMS_ALGORITHMS.toList().associate { it.first to FakeKMS::class.java.loadKeyPairs(it) }
        }
    }

    /**
     * Convenience function to get a KMS client
     */
    fun client() = KMS.Http(Region.of("ldn-north-1"), { AwsCredentials("accessKey", "secret") }, this)
}

fun main() {
    FakeKMS().start()
}

