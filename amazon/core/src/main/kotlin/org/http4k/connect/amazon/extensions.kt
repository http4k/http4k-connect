package org.http4k.connect.amazon

import org.http4k.aws.AwsCredentials
import org.http4k.connect.amazon.model.Region

fun Map<String, String>.awsRegion() = Region.of(getValue("AWS_REGION"))

fun Map<String, String>.awsCredentials(): () -> AwsCredentials =
    {
        AwsCredentials(
            getValue("AWS_ACCESS_KEY_ID"),
            getValue("AWS_SECRET_ACCESS_KEY"),
            get("AWS_SESSION_TOKEN")
        )
    }
