@file:Suppress("unused")

package myplugin.oauth

import org.http4k.serverless.ApiGatewayV2LambdaFunction

/**
 * Bind the plugin to an AWS Serverless function
 */
class OAuthPlugInLambda : ApiGatewayV2LambdaFunction(OAuthPlugin())
