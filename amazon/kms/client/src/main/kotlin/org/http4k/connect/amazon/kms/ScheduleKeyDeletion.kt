package org.http4k.connect.amazon.kms

import org.http4k.connect.amazon.model.KmsKeyId
import org.http4k.connect.amazon.model.Timestamp

data class ScheduleKeyDeletion(val KeyId: KmsKeyId, val PendingWindowInDays: Int? = null)
    : KMSAction<KeyDeletionSchedule>(KeyDeletionSchedule::class)

data class KeyDeletionSchedule(val KeyId: KmsKeyId, val DeletionDate: Timestamp)
