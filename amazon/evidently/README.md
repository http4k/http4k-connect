# Cloudwatch Evidently

The Evidently connector provides the following Actions:

     *  CreateProject
     *  CreateFeature
     *  EvaluateFeature
     *  BatchEvaluateFeature
     *  DeleteFeature
     *  DeleteProject

### Example usage

```kotlin
const val USE_REAL_CLIENT = false

fun main() {
    // we can connect to the real service or the fake (drop in replacement)
    val http: HttpHandler = if (USE_REAL_CLIENT) JavaHttpClient() else FakeEvidently()

    // create a client
    val client = Evidently.Http(Region.of("us-east-1"), { AwsCredentials("accessKeyId", "secretKey") }, http.debug())

    val projectName = ProjectName.of("acme-service")
    val featureName = FeatureName.of("take-over-the-world")

    // create project
    client.createProject(projectName)
        .onFailure { it.reason.throwIt() }

    // create feature
    client.createFeature(
        project = projectName,
        name = featureName,
        defaultVariation = VariationName.of("bide-our-time"),
        variations = mapOf(
            VariationName.of("bide-our-time") to VariableValue(false),
            VariationName.of("it-is-time") to VariableValue(true)
        ),
        entityOverrides = mapOf(
            EntityId.of("test-subject-1") to VariationName.of("it-is-time")
        )
    ).onFailure { it.reason.throwIt() }

    // evaluate feature
    val result = client.evaluateFeature(projectName, featureName, EntityId.of("test-subject-2"))
        .onFailure { it.reason.throwIt() }

    println(result)
}
```

The client APIs utilise the `http4k-aws` module for request signing, which means no dependencies on the incredibly fat
Amazon-SDK JARs. This means this integration is perfect for running Serverless Lambdas where binary size is a
performance factor.

### Default Fake port: 45011

To start:

```
FakeEvidently().start()
```
