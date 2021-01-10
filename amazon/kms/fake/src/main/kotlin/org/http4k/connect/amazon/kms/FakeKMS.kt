package org.http4k.connect.amazon.kms

import org.http4k.aws.AwsCredentials
import org.http4k.connect.ChaosFake
import org.http4k.connect.amazon.AmazonJsonFake
import org.http4k.connect.amazon.model.AwsService
import org.http4k.connect.amazon.model.Base64Blob
import org.http4k.connect.amazon.model.Region
import org.http4k.connect.storage.InMemory
import org.http4k.connect.storage.Storage
import org.http4k.core.Method.POST
import org.http4k.routing.bind
import org.http4k.routing.routes
import java.time.Clock

class FakeKMS(
    keys: Storage<StoredCMK> = Storage.InMemory(),
    private val clock: Clock = Clock.systemDefaultZone()
) : ChaosFake() {

    private val api = AmazonJsonFake(KMSMoshi, AwsService.of("TrentService"))

    private val publicKey by lazy {
        Base64Blob.encoded(this::class.java.classLoader.getResource("id_example.pub")!!.readText())
    }

    private val privateKey by lazy {
        Base64Blob.encoded(this::class.java.classLoader.getResource("id_example")!!.readText())
    }

    override val app = routes(
        "/" bind POST to routes(
            api.createKey(keys),
            api.describeKey(keys),
            api.decrypt(keys),
            api.encrypt(keys),
            api.getPublicKey(keys, publicKey),
            api.scheduleKeyDeletion(keys),
            api.sign(keys),
            api.verify(keys)
        )
    )

    /**
     * Convenience function to get a KMS client
     */
    fun client() = KMS.Http(Region.of("ldn-north-1"), { AwsCredentials("accessKey", "secret") }, this, clock)
}


fun main() {
    FakeKMS().start()
}
