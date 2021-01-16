package org.http4k.format

import org.http4k.connect.amazon.model.ARN
import org.http4k.connect.amazon.model.AccessKeyId
import org.http4k.connect.amazon.model.AwsAccount
import org.http4k.connect.amazon.model.AwsService
import org.http4k.connect.amazon.model.Base64Blob
import org.http4k.connect.amazon.model.KMSKeyId
import org.http4k.connect.amazon.model.Region
import org.http4k.connect.amazon.model.SecretAccessKey
import org.http4k.connect.amazon.model.SessionToken
import org.http4k.connect.amazon.model.Timestamp
import org.http4k.lens.BiDiMapping
import value

fun <T> AutoMappingConfiguration<T>.withAwsCoreMappings() = apply {
    value(AccessKeyId)
    value(ARN)
    value(AwsService)
    value(AwsAccount)
    value(Base64Blob)
    value(KMSKeyId)
    value(Region)
    value(SecretAccessKey)
    value(SessionToken)
    double(BiDiMapping({ Timestamp.of(it.toLong()) }, { it.value.toDouble() }))
}
