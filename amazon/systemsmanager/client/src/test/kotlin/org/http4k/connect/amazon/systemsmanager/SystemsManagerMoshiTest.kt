package org.http4k.connect.amazon.systemsmanager

import org.http4k.connect.SystemMoshiContract
import org.http4k.connect.amazon.model.KMSKeyId
import org.http4k.connect.amazon.model.ParameterType
import org.http4k.connect.amazon.model.SSMParameterName
import org.http4k.connect.amazon.model.Tag
import org.http4k.connect.amazon.systemsmanager.action.DeleteParameter
import org.http4k.connect.amazon.systemsmanager.action.GetParameter
import org.http4k.connect.amazon.systemsmanager.action.PutParameter
import org.http4k.connect.randomString

class SystemsManagerMoshiTest : SystemMoshiContract(
    SystemsManagerMoshi,
    DeleteParameter(SSMParameterName.of(randomString)),
    GetParameter(SSMParameterName.of(randomString), true),
    PutParameter(SSMParameterName.of(randomString), randomString, ParameterType.SecureString, KMSKeyId.of(randomString), true,
        randomString, randomString, randomString, listOf(randomString), listOf(Tag(randomString, randomString)), randomString)
)
