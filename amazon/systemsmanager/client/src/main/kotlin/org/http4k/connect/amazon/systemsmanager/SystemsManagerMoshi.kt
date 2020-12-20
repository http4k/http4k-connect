package org.http4k.connect.amazon.systemsmanager

import com.squareup.moshi.Moshi
import org.http4k.format.ConfigurableMoshi
import org.http4k.format.asConfigurable
import org.http4k.format.withAwsCoreMappings
import org.http4k.format.withStandardMappings

object SystemsManagerMoshi : ConfigurableMoshi(Moshi.Builder()
    .add(KotshiSystemsManagerAdapterFactory)
    .asConfigurable()
    .withStandardMappings()
    .withAwsCoreMappings()
    .done()
)
