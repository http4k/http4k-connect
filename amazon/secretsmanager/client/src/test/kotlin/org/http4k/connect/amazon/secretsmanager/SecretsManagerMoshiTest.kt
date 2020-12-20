package org.http4k.connect.amazon.secretsmanager

import org.http4k.connect.SystemMoshiContract
import org.http4k.connect.amazon.model.KMSKeyId
import org.http4k.connect.amazon.model.SecretId
import org.http4k.connect.amazon.model.Tag
import org.http4k.connect.amazon.model.VersionId
import org.http4k.connect.amazon.model.VersionStage
import org.http4k.connect.amazon.secretsmanager.action.CreateSecret
import org.http4k.connect.amazon.secretsmanager.action.DeleteSecret
import org.http4k.connect.amazon.secretsmanager.action.Filter
import org.http4k.connect.amazon.secretsmanager.action.GetSecretValue
import org.http4k.connect.amazon.secretsmanager.action.ListSecrets
import org.http4k.connect.amazon.secretsmanager.action.PutSecretValue
import org.http4k.connect.amazon.secretsmanager.action.SortOrder
import org.http4k.connect.amazon.secretsmanager.action.UpdateSecret
import org.http4k.connect.randomString
import java.util.UUID

val KeyId = KMSKeyId.of(randomString)

val ASecretId = SecretId.of(randomString)

class SecretsManagerMoshiTest : SystemMoshiContract(
    SecretsManagerMoshi,
    CreateSecret(randomString, UUID.randomUUID(), randomString, randomString, KeyId, listOf(Tag(randomString, randomString))),
    DeleteSecret(ASecretId, true, 1),
    GetSecretValue(ASecretId, VersionId.of(randomString), VersionStage.of(randomString)),
    ListSecrets(1, randomString, SortOrder.asc, listOf(Filter(randomString, listOf(randomString)))),
    PutSecretValue(ASecretId, UUID.randomUUID(), randomString, listOf(VersionStage.of(randomString))),
    UpdateSecret(ASecretId, UUID.randomUUID(), randomString, randomString, KeyId)
)
