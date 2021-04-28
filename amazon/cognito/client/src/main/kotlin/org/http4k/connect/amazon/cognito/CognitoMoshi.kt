package org.http4k.connect.amazon.cognito

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import org.http4k.connect.amazon.cognito.model.AccessToken
import org.http4k.connect.amazon.cognito.model.ClientId
import org.http4k.connect.amazon.cognito.model.ClientName
import org.http4k.connect.amazon.cognito.model.ClientSecret
import org.http4k.connect.amazon.cognito.model.IdToken
import org.http4k.connect.amazon.cognito.model.Password
import org.http4k.connect.amazon.cognito.model.PoolName
import org.http4k.connect.amazon.cognito.model.RefreshToken
import org.http4k.connect.amazon.cognito.model.SecretCode
import org.http4k.connect.amazon.cognito.model.Session
import org.http4k.connect.amazon.cognito.model.UserCode
import org.http4k.connect.amazon.cognito.model.UserPoolId
import org.http4k.connect.amazon.cognito.model.Username
import org.http4k.format.AwsCoreJsonAdapterFactory
import org.http4k.format.ConfigurableMoshi
import org.http4k.format.asConfigurable
import org.http4k.format.value
import org.http4k.format.withAwsCoreMappings
import org.http4k.format.withStandardMappings
import se.ansman.kotshi.KotshiJsonAdapterFactory

object CognitoMoshi : ConfigurableMoshi(
    Moshi.Builder()
        .add(KotshiCognitoJsonAdapterFactory)
        .add(AwsCoreJsonAdapterFactory())
        .asConfigurable()
        .withStandardMappings()
        .withAwsCoreMappings()
        .value(AccessToken)
        .value(ClientId)
        .value(ClientName)
        .value(ClientSecret)
        .value(IdToken)
        .value(RefreshToken)
        .value(Password)
        .value(PoolName)
        .value(SecretCode)
        .value(Session)
        .value(UserCode)
        .value(Username)
        .value(UserPoolId)
        .done()
)

@KotshiJsonAdapterFactory
abstract class CognitoJsonAdapterFactory : JsonAdapter.Factory
