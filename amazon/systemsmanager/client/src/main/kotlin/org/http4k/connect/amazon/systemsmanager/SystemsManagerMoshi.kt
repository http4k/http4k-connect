package org.http4k.connect.amazon.systemsmanager

import com.squareup.moshi.Moshi
import org.http4k.connect.adapter
import org.http4k.connect.amazon.model.SSMParameterName
import org.http4k.connect.amazon.systemsmanager.action.KotshiDeleteParameterJsonAdapter
import org.http4k.connect.amazon.systemsmanager.action.KotshiGetParameterJsonAdapter
import org.http4k.connect.amazon.systemsmanager.action.KotshiParameterJsonAdapter
import org.http4k.connect.amazon.systemsmanager.action.KotshiParameterValueJsonAdapter
import org.http4k.connect.amazon.systemsmanager.action.KotshiPutParameterJsonAdapter
import org.http4k.connect.amazon.systemsmanager.action.KotshiPutParameterResultJsonAdapter
import org.http4k.format.AutoMappingConfiguration
import org.http4k.format.AwsJsonAdapterFactory
import org.http4k.format.ConfigurableMoshi
import org.http4k.format.asConfigurable
import org.http4k.format.text
import org.http4k.format.withAwsCoreMappings
import org.http4k.format.withStandardMappings

object SystemsManagerMoshi : ConfigurableMoshi(Moshi.Builder()
    .add(SystemsManagerJsonAdapterFactory)
    .asConfigurable()
    .withStandardMappings()
    .withAwsCoreMappings()
    .withSystemsManagerMappings()
    .done()
)

fun <T> AutoMappingConfiguration<T>.withSystemsManagerMappings() = apply {
    text(SSMParameterName::of)
}

object SystemsManagerJsonAdapterFactory : AwsJsonAdapterFactory(
    adapter(::KotshiDeleteParameterJsonAdapter),
    adapter(::KotshiGetParameterJsonAdapter),
    adapter(::KotshiParameterJsonAdapter),
    adapter(::KotshiParameterValueJsonAdapter),
    adapter(::KotshiPutParameterJsonAdapter),
    adapter { KotshiPutParameterResultJsonAdapter() }
)
