# IAM Identity Center

The IAMIdentityCenter connector provides the following Fakes:

## OIDC

Actions:
* RegisterClient
* StartDeviceAuthentication
* CreateToken

### Default Fake port: 34160

To start:

```
FakeOIDC().start()
```

## SSO

Actions:
* SSO: GetFederatedCredentials

### Default Fake port: 25813

To start:

```
FakeSSO().start()
```

## Interactive CLI login

The module provides a CredentialsProvider to do interactive login to

```kotlin
val provider = CredentialsProvider.SSO(
    SSOProfile(
        AwsAccount.of("01234567890"),
        RoleName.of("hello"),
        Region.US_EAST_1,
        Uri.of("http://foobar"),
    )
)
```
