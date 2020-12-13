package org.http4k.connect.amazon.kms.action

import org.http4k.connect.amazon.model.KMSKeyId
import org.http4k.connect.amazon.model.Timestamp

data class ScheduleKeyDeletion(val KeyId: KMSKeyId, val PendingWindowInDays: Int? = null)
    : KMSAction<KeyDeletionSchedule>(KeyDeletionSchedule::class)

data class KeyDeletionSchedule(val KeyId: KMSKeyId, val DeletionDate: Timestamp)
