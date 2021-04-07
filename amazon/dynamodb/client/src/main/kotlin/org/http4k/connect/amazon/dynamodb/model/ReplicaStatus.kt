package org.http4k.connect.amazon.dynamodb.model

enum class ReplicaStatus {
    CREATING, CREATION_FAILED, UPDATING, DELETING, ACTIVE, REGION_DISABLED, INACCESSIBLE_ENCRYPTION_CREDENTIALS
}
