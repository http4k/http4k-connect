package org.http4k.connect.amazon.lambda

import com.squareup.moshi.Moshi
import org.http4k.connect.amazon.model.FunctionName
import org.http4k.format.AutoMappingConfiguration
import org.http4k.format.ConfigurableMoshi
import org.http4k.format.asConfigurable
import org.http4k.format.text
import org.http4k.format.withAwsCoreMappings
import org.http4k.format.withStandardMappings

object LambdaMoshi : ConfigurableMoshi(Moshi.Builder()
    .asConfigurable()
    .withStandardMappings()
    .withAwsCoreMappings()
    .withLambdaMappings()
    .done()
)

fun <T> AutoMappingConfiguration<T>.withLambdaMappings() = apply {
    text(FunctionName::of)
}
