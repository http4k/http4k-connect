package org.http4k.connect.amazon.s3

import dev.forkhandles.result4k.map
import org.http4k.connect.amazon.model.BucketKey
import org.http4k.connect.amazon.model.BucketName

fun main() {
    val fakeS3 = FakeS3()
    val s3BucketClient = fakeS3.s3BucketClient(BucketName.of("foobar"))
    val s3Client = fakeS3.s3Client()

    println(s3BucketClient(CreateRequest()))
    println(s3Client(ListBucketsRequest()))

    println(s3BucketClient.set(BucketKey.of("content"), "hellothere".byteInputStream()))
    println(s3BucketClient(ListKeysRequest()))
    println(s3BucketClient[BucketKey.of("content")].map { it!!.bufferedReader().readText() })
}
