package org.http4k.connect.amazon

import org.http4k.cloudnative.env.EnvironmentKey
import org.http4k.lens.uri


val AWS_CONTAINER_CREDENTIALS_RELATIVE_URI = EnvironmentKey.uri().required("AWS_CONTAINER_CREDENTIALS_RELATIVE_URI")
