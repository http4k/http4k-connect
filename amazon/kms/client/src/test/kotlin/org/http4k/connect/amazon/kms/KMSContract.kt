package org.http4k.connect.amazon.kms

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.present
import org.http4k.connect.amazon.AwsContract
import org.http4k.connect.amazon.model.AwsService
import org.http4k.connect.successValue
import org.http4k.core.HttpHandler
import org.http4k.core.then
import org.http4k.filter.DebuggingFilters
import org.junit.jupiter.api.Test

abstract class KMSContract(http: HttpHandler) : AwsContract(AwsService.of("kms"), http) {
    private val kms by lazy {
        KMS.Http(aws.scope, { aws.credentials }, DebuggingFilters.PrintRequestAndResponse().then(http))
    }

    @Test
    fun `key lifecycle`() {
        with(kms) {
            val creation = createKey(CreateKey.Request()).successValue()
            val keyId = creation.KeyMetadata.KeyId
            assertThat(keyId, present())

            val describe = describeKey(DescribeKey.Request(keyId)).successValue()
            assertThat(describe.KeyMetadata.KeyId, equalTo(keyId))

            val deletion = scheduleKeyDelete(ScheduleKeyDeletion.Request(keyId)).successValue()
            assertThat(deletion.KeyId, equalTo(keyId))

            val failedDeletion = scheduleKeyDelete(ScheduleKeyDeletion.Request(keyId)).successValue()
            assertThat(failedDeletion.KeyId, equalTo(keyId))
//
//            val updated = update(UpdateSecret.Request(keyId, UUID.randomUUID(), updatedValue)).successValue()
//            assertThat(updated!!.Name, present(equalTo(name)))
//
//            val putValue = put(PutSecret.Request(keyId, UUID.randomUUID(), finalValue)).successValue()
//            assertThat(putValue!!.Name, present(equalTo(name)))
//
//            val lookupUpdated = describe(DescribeKey.Request(keyId)).successValue()
//            assertThat(lookupUpdated?.SecretString, present(equalTo(finalValue)))
//
//            val deleted = delete(DeleteSecret.Request(keyId)).successValue()
//            assertThat(deleted?.arn, present(equalTo(updated.arn)))
//
//            val lookupDeleted = describe(DescribeKey.Request(keyId)).successValue()
//            assertThat(lookupDeleted, absent())
        }
    }
}
