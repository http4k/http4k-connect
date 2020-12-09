package org.http4k.connect.amazon.systemsmanager


fun main() {
    val a = """{"Parameter":{"ARN":"arn:aws:ssm:eu-central-1:169766454405:parameter/8d11f42b-9747-496e-9114-c15a80d32bfa","DataType":"text","LastModifiedDate":1.607453621072E9,"Name":"8d11f42b-9747-496e-9114-c15a80d32bfa","Type":"String","Value":"value","Version":1}}
"""

    SystemsManagerMoshi.asA<GetParameter.Response>(a)
}
