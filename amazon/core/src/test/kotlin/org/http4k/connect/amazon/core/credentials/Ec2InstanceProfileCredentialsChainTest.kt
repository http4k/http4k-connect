package org.http4k.connect.amazon.core.credentials

import com.natpryce.hamkrest.absent
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.http4k.aws.AwsCredentials
import org.http4k.core.HttpHandler
import org.http4k.core.Method
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.junit.jupiter.api.Test

class Ec2InstanceProfileCredentialsChainTest {

    private val credentialsBody = """
      {
        "Code" : "Success",
        "LastUpdated" : "2022-07-03T22:16:43Z",
        "Type" : "AWS-HMAC",
        "AccessKeyId" : "AWS123",
        "SecretAccessKey" : "ABC/123",
        "Token" : "DEF/123/GHI",
        "Expiration" : "2022-07-04T04:29:13Z"
      }
    """

    @Test
    fun `metadata service not available (not in EC2)`() {
        val server: HttpHandler = { Response(Status.CONNECTION_REFUSED) }

        val chain = CredentialsChain.Ec2InstanceProfile(
            Ec2InstanceMetadataClient(server)
        )

        assertThat(chain.invoke(), absent())
    }

    @Test
    fun `no instance profile available`() {
        val server = routes(
            "/latest/meta-data/iam/security-credentials" bind Method.GET to {
                Response(Status.OK)
            },
        )

        val chain = CredentialsChain.Ec2InstanceProfile(
            Ec2InstanceMetadataClient(server)
        )

        assertThat(chain.invoke(), absent())
    }

    @Test
    fun `load credentials from profile`() {
        val server = routes(
            "/latest/meta-data/iam/security-credentials" bind Method.GET to {
                Response(Status.OK).body("service-role")
            },
            "/latest/meta-data/iam/security-credentials/service-role" bind Method.GET to {
                Response(Status.OK).body(credentialsBody)
            }
        )

        val chain = CredentialsChain.Ec2InstanceProfile(
            Ec2InstanceMetadataClient(server)
        )

        assertThat(
            chain.invoke(),
            equalTo(AwsCredentials("AWS123", "ABC/123", "DEF/123/GHI"))
        )
    }
}
