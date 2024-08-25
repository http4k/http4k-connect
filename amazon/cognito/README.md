# Cognito

### Installation

```kotlin
dependencies {
    implementation(platform("org.http4k:http4k-connect-bom:5.22.1.0"))
    implementation("org.http4k:http4k-connect-amazon-cognito")
    implementation("org.http4k:http4k-connect-amazon-cognito-fake")
}
```


The Cognito connector provides the following Actions:

- AdminCreateUser
- AdminDeleteUser
- AdminDisableUser
- AdminEnableUser
- AdminGetUser
- AdminResetUserPassword
- AdminSetUserPassword
- AssociateSoftwareToken
- ConfirmForgotPassword
- CreateResourceServer
- CreateUserPool
- CreateUserPoolClient
- CreateUserPoolDomain
- DeleteUserPool
- DeleteUserPoolClient
- DeleteUserPoolDomain
- ForgotPassword
- GetJwks
- ListUserPools
- InitiateAuth
- RespondToAuthChallenge
- VerifySoftwareToken

## # Fake

The Cognito Fake has very limited functionality for creating User Pools and User Pool Clients only.
It can act as an OAuthServer for created User Pool Clients. It supports the ClientCredentials and
Authorization Code grants and returns JWTs which have been signed with a private key. The matching public key can be
retrieved from the following endpoint:

`http://<server:port>/<user pool id>/.well-known/jwks.json`

Note that there are 2 keys returned by the JWKs endpoint - the first is "expired" and not used, the second is the one
used to sign the JWTs.

### Default Fake port: 37192

To start:

```
FakeCloudFront().start()
```
