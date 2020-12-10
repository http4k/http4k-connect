package org.http4k.connect.amazon

import org.http4k.template.ViewModel

data class AssumeRoleResponseView(
    val arn: String,
    val roleId: String,
    val accessKeyId: String,
    val secretAccessKey: String,
    val sessionToken: String,
    val expiration: String,
) : ViewModel
