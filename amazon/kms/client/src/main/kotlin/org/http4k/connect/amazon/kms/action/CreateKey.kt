package org.http4k.connect.amazon.kms.action

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.Moshi
import org.http4k.connect.Http4kConnectAction
import org.http4k.connect.Http4kConnectMoshiAdapter
import org.http4k.connect.amazon.model.CustomerMasterKeySpec
import org.http4k.connect.amazon.model.KeyMetadata
import org.http4k.connect.amazon.model.KeyUsage
import org.http4k.connect.amazon.model.Tag
import org.http4k.format.ConfigurableMoshi
import org.http4k.format.asConfigurable
import org.http4k.format.withAwsCoreMappings
import org.http4k.format.withStandardMappings
import java.lang.reflect.Type

@Http4kConnectAction
data class CreateKey(
    val CustomerMasterKeySpec: CustomerMasterKeySpec? = null,
    val KeyUsage: KeyUsage? = null,
    val BypassPolicyLockoutSafetyCheck: Boolean? = null,
    val CustomKeyStoreId: String? = null,
    val Description: String? = null,
    val Origin: String? = null,
    val Policy: String? = null,
    val Tags: List<Tag>? = null
) : KMSAction<KeyCreated>(KeyCreated::class)

data class KeyCreated(val KeyMetadata: KeyMetadata)

data class CreateKey2(
    val Description: String? = null
)

object KMSMoshiAdapterFactory : JsonAdapter.Factory {
    override fun create(type: Type, annotations: Set<Annotation>, moshi: Moshi): JsonAdapter<*>? =
        when (type.typeName) {
            CreateKey2::class.java.typeName -> CreateKey2JsonAdapter1(moshi).nullSafe()
            else -> null
        }
}

class CreateKey2JsonAdapter1(moshi: Moshi) : Http4kConnectMoshiAdapter<CreateKey2>() {
    private val description = moshi.adapter(String::class.java)

    override fun fromJsonFields(fields: Map<*, *>) =
        CreateKey2(
            fields["Description"]?.let(description::fromJsonValue)
        )

    override fun fromObject(writer: JsonWriter, it: CreateKey2) {
        writer.name("Description")
        description.toJson(writer, it.Description)
    }
}

object CustomMoshi : ConfigurableMoshi(Moshi.Builder()
    .add(KMSMoshiAdapterFactory)
    .asConfigurable()
    .withStandardMappings()
    .withAwsCoreMappings()
    .done()
)

fun main() {
    val message = CustomMoshi.asA<CreateKey2>("""{"Description":"s3"}""")
    println(message)
    println(CustomMoshi.asFormatString(message))
}
