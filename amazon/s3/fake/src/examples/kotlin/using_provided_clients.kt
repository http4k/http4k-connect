package org.http4k.connect.amazon.s3

import dev.forkhandles.result4k.map
import org.http4k.connect.amazon.model.BucketKey
import org.http4k.connect.amazon.model.BucketName

fun main() {
    val fakeS3 = FakeS3()
    val s3BucketClient = fakeS3.s3BucketClient(BucketName("foobar"))
    val s3Client = fakeS3.s3Client()

    println(s3BucketClient.create())
    println(s3Client.buckets())

    println(s3BucketClient.set(BucketKey("content"), "hellothere".byteInputStream()))
    println(s3BucketClient.list())
    println(s3BucketClient[BucketKey("content")].map { it!!.bufferedReader().readText() })
}
