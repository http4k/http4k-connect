package org.http4k.connect.amazon.s3

data class BucketName(val name: String) {
    override fun toString() = name
}

data class BucketKey(val value: String) {
    override fun toString() = value
}
