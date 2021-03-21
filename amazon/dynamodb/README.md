# DynamoDb

The DynamoDb connector provides the following Actions:

    * CreateTable
    * DeleteTable
    * DescribeTable
    * ListTables
    * UpdateTable

    * DeleteItem
    * GetItem
    * PutItem
    * Query
    * UpdateItem

    * TransactGetItems
    * TransactWriteItems

    * BatchGetItem
    * BatchWriteItem

    * ExecuteTransaction
    * ExecuteStatement
    * BatchExecuteStatement

### Example usage

```kotlin
    // we can connect to the real service
val http: HttpHandler = JavaHttpClient()

// create a client
val client = DynamoDb.Http(Region.of("us-east-1"), { AwsCredentials("accessKeyId", "secretKey") }, http.debug())

// all operations return a Result monad of the API type
val deleteResult: Result<TableDescriptionResponse, RemoteFailure> = client.deleteTable(TableName.of("myTable"))
println(deleteResult)
```

Note that there currently is no Fake implementation of the Dynamo adapter. You can use [DynamoDB](https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/DynamoDBLocal.DownloadingAndRunning.html) local instead


The client APIs utilise the `http4k-aws` module for request signing, which means no dependencies on the incredibly fat
Amazon-SDK JARs. This means this integration is perfect for running Serverless Lambdas where binary size is a
performance factor.
