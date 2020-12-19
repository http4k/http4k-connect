package org.http4k.connect.amazon.s3

import dev.forkhandles.result4k.map
import org.http4k.connect.amazon.model.BucketKey
import org.http4k.connect.amazon.model.BucketName
import org.http4k.connect.amazon.model.Region

fun main() {
    val fakeS3 = FakeS3()
    val name = BucketName.of("foobar")
    val region = Region.of("ldn-north-1")
    val s3BucketClient = fakeS3.s3BucketClient(name, region)
    val s3Client = fakeS3.s3Client()

    println(s3Client.createBucket(name, region))
    println(s3Client.listBuckets())

    println(s3BucketClient.set(BucketKey.of("content"), "hellothere".byteInputStream()))
    println(s3BucketClient.listKeys())
    println(s3BucketClient[BucketKey.of("content")].map { it!!.bufferedReader().readText() })
}
