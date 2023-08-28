package org.http4k.connect.amazon.iamidentitycenter

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import org.http4k.connect.amazon.model.Code
import org.http4k.connect.amazon.model.DeviceCode
import org.http4k.connect.amazon.model.ExpiresIn
import org.http4k.connect.amazon.model.Interval
import org.http4k.connect.amazon.model.Scope
import org.http4k.connect.amazon.model.SessionId
import org.http4k.connect.amazon.model.TokenType
import org.http4k.format.AwsCoreJsonAdapterFactory
import org.http4k.format.ConfigurableMoshi
import org.http4k.format.ListAdapter
import org.http4k.format.MapAdapter
import org.http4k.format.asConfigurable
import org.http4k.format.value
import org.http4k.format.withAwsCoreMappings
import org.http4k.format.withStandardMappings
import se.ansman.kotshi.KotshiJsonAdapterFactory

object IAMIdentityCenterMoshi : ConfigurableMoshi(
    Moshi.Builder()
        .add(IAMIdentityCenterJsonAdapterFactory)
        .add(AwsCoreJsonAdapterFactory())
        .add(ListAdapter)
        .add(MapAdapter)
        .asConfigurable()
        .value(Code)
        .value(DeviceCode)
        .value(Scope)
        .value(Interval)
        .value(ExpiresIn)
        .value(SessionId)
        .value(TokenType)
        .withStandardMappings()
        .withAwsCoreMappings()
        .done()
)

@KotshiJsonAdapterFactory
object IAMIdentityCenterJsonAdapterFactory : JsonAdapter.Factory by KotshiIAMIdentityCenterJsonAdapterFactory
