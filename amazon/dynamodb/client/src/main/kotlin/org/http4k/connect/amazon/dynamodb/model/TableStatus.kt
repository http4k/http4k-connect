package org.http4k.connect.amazon.dynamodb.model

enum class TableStatus {
    CREATING, UPDATING, DELETING, ACTIVE, INACCESSIBLE_ENCRYPTION_CREDENTIALS, ARCHIVING, ARCHIVED
}
