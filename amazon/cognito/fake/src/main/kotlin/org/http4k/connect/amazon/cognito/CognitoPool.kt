package org.http4k.connect.amazon.cognito

import org.http4k.connect.amazon.cognito.action.CreateResourceServer
import org.http4k.connect.amazon.cognito.model.CloudFrontDomain
import org.http4k.connect.amazon.cognito.model.PoolName
import org.http4k.connect.amazon.cognito.model.UserPoolClient

data class CognitoPool(
    val name: PoolName,
    val clients: MutableList<UserPoolClient> = mutableListOf(),
    val domains: MutableList<CloudFrontDomain> = mutableListOf(),
    val resourceServers: MutableList<CreateResourceServer> = mutableListOf()
)
