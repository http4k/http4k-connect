package org.http4k.connect.amazon.containercredentials

import com.squareup.moshi.Moshi
import org.http4k.format.AwsCoreJsonAdapterFactory
import org.http4k.format.ConfigurableMoshi
import org.http4k.format.asConfigurable
import org.http4k.format.withAwsCoreMappings
import org.http4k.format.withStandardMappings

object ContainerCredentialsMoshi : ConfigurableMoshi(
    Moshi.Builder()
        .add(AwsCoreJsonAdapterFactory())
        .asConfigurable()
        .withStandardMappings()
        .withAwsCoreMappings()
        .done()
)
