package org.http4k.connect.amazon.systemsmanager

import com.squareup.moshi.Moshi
import org.http4k.connect.ActionAdapterFactory
import org.http4k.connect.adapter
import org.http4k.connect.amazon.systemsmanager.action.KotshiDeleteParameterJsonAdapter
import org.http4k.connect.amazon.systemsmanager.action.KotshiGetParameterJsonAdapter
import org.http4k.connect.amazon.systemsmanager.action.KotshiParameterJsonAdapter
import org.http4k.connect.amazon.systemsmanager.action.KotshiParameterValueJsonAdapter
import org.http4k.connect.amazon.systemsmanager.action.KotshiPutParameterJsonAdapter
import org.http4k.connect.amazon.systemsmanager.action.KotshiPutParameterResultJsonAdapter
import org.http4k.format.ConfigurableMoshi
import org.http4k.format.asConfigurable
import org.http4k.format.withAwsCoreMappings
import org.http4k.format.withStandardMappings

object SystemsManagerMoshi : ConfigurableMoshi(Moshi.Builder()
    .add(SystemsManagerAdapterFactory)
    .asConfigurable()
    .withStandardMappings()
    .withAwsCoreMappings()
    .done()
)

object SystemsManagerAdapterFactory : ActionAdapterFactory(
    adapter { KotshiDeleteParameterJsonAdapter() },
    adapter { KotshiGetParameterJsonAdapter() },
    adapter(::KotshiParameterJsonAdapter),
    adapter(::KotshiParameterValueJsonAdapter),
    adapter(::KotshiPutParameterJsonAdapter),
    adapter { KotshiPutParameterResultJsonAdapter() }
)
