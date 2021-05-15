package org.http4k.format

import org.http4k.connect.amazon.core.model.ARN
import org.http4k.connect.amazon.core.model.AccessKeyId
import org.http4k.connect.amazon.core.model.AwsAccount
import org.http4k.connect.amazon.core.model.AwsService
import org.http4k.connect.amazon.core.model.Base64Blob
import org.http4k.connect.amazon.core.model.KMSKeyId
import org.http4k.connect.amazon.core.model.Password
import org.http4k.connect.amazon.core.model.Region
import org.http4k.connect.amazon.core.model.SecretAccessKey
import org.http4k.connect.amazon.core.model.SessionToken
import org.http4k.connect.amazon.core.model.Timestamp
import org.http4k.connect.amazon.core.model.Username
import org.http4k.lens.BiDiMapping

fun <T> AutoMappingConfiguration<T>.withAwsCoreMappings() = apply {
    value(AccessKeyId)
    value(ARN)
    value(AwsAccount)
    value(AwsService)
    value(Base64Blob)
    value(KMSKeyId)
    value(Password)
    value(Region)
    value(SecretAccessKey)
    value(SessionToken)
    double(BiDiMapping({ Timestamp.of(it.toLong()) }, { it.value.toDouble() }))
    value(Username)
}
