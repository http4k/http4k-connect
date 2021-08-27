package org.http4k.connect.amazon.sts.action

import org.http4k.connect.amazon.sts.model.AssumedRoleUser
import org.http4k.connect.amazon.sts.model.Credentials

interface AssumedRole {
    val AssumedRoleUser: AssumedRoleUser
    val Credentials: Credentials
}
