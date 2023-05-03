package org.http4k.connect.amazon.s3.action

@Deprecated("Renamed to match API", ReplaceWith("CopyObject"))
typealias CopyKey = CopyObject

@Deprecated("Renamed to match API", ReplaceWith("DeleteObject"))
typealias DeleteKey = DeleteObject

@Deprecated("Renamed to match API", ReplaceWith("GetObject"))
typealias GetKey = GetObject

@Deprecated("Renamed to match API", ReplaceWith("PutObject"))
typealias PutKey = PutObject

@Deprecated("Moved", ReplaceWith("org.http4k.connect.amazon.s3.S3BucketAction<T>"))
typealias S3BucketAction<T> = org.http4k.connect.amazon.s3.S3BucketAction<T>

@Deprecated("Moved", ReplaceWith("org.http4k.connect.amazon.s3.S3Action<T>"))
typealias S3Action<T> = org.http4k.connect.amazon.s3.S3Action<T>
