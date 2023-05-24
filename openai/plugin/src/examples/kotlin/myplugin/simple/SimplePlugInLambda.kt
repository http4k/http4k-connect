@file:Suppress("unused")

package myplugin.simple

import myplugin.oauth.OAuthPlugin
import org.http4k.serverless.ApiGatewayV2LambdaFunction

/**
 * Bind the plugin to an AWS Serverless function
 */
class SimplePlugInLambda : ApiGatewayV2LambdaFunction(OAuthPlugin())
