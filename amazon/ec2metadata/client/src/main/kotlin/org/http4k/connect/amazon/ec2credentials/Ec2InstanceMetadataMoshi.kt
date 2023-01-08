package org.http4k.connect.amazon.ec2credentials

import com.squareup.moshi.Moshi
import org.http4k.connect.amazon.ec2credentials.model.HostName
import org.http4k.connect.amazon.ec2credentials.model.ImageId
import org.http4k.connect.amazon.ec2credentials.model.InstanceId
import org.http4k.connect.amazon.ec2credentials.model.InstanceType
import org.http4k.format.AwsCoreJsonAdapterFactory
import org.http4k.format.ConfigurableMoshi
import org.http4k.format.asConfigurable
import org.http4k.format.value
import org.http4k.format.withAwsCoreMappings
import org.http4k.format.withStandardMappings

object Ec2InstanceMetadataMoshi : ConfigurableMoshi(
    Moshi.Builder()
        .add(AwsCoreJsonAdapterFactory())
        .asConfigurable()
        .withStandardMappings()
        .withAwsCoreMappings()
        .value(HostName)
        .value(ImageId)
        .value(InstanceId)
        .value(InstanceType)
        .done()
)
