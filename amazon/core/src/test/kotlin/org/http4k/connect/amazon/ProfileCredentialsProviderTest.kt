package org.http4k.connect.amazon

import com.natpryce.hamkrest.absent
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.http4k.aws.AwsCredentials
import org.http4k.cloudnative.env.Environment
import org.http4k.connect.amazon.core.model.ProfileName
import org.http4k.core.with
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import java.nio.file.Files

class ProfileCredentialsProviderTest {

    private val profileFile = Files.createTempFile("credentials", "ini").also {
        it.toFile().writeText("""
            [default]
            aws_access_key_id = key123
            aws_secret_access_key = secret123
            
            [dev]
            aws_access_key_id = key456
            aws_secret_access_key = secret456
        """)
    }

    private val env = Environment.EMPTY
        .with(AWS_CREDENTIAL_PROFILES_FILE of profileFile)

    @AfterEach
    fun cleanup() {
        profileFile.toFile().delete()
    }

    @Test
    fun `default profile in custom file`() {
        assertThat(
            CredentialsChain.Profile(env).invoke(),
            equalTo(AwsCredentials("key123", "secret123"))
        )
    }

    @Test
    fun `custom profile in custom file`() {
        val env = env.with(AWS_PROFILE of ProfileName.of("dev"))

        assertThat(
            CredentialsChain.Profile(env)(),
            equalTo(AwsCredentials("key456", "secret456"))
        )
    }

    @Test
    fun `file has no default profile`() {
        profileFile.toFile().writeText("""
            [dev]
            aws_access_key_id = key456
            aws_secret_access_key = secret456
        """)

        assertThat(
            CredentialsChain.Profile(Environment.EMPTY)(),
            absent()
        )
    }

    @Test
    fun `custom profile not found`() {
        val env = env.with(AWS_PROFILE of ProfileName.of("prod"))

        assertThat(
            CredentialsChain.Profile(env).invoke(),
            absent()
        )
    }

    @Test
    fun `missing file`() {
        assertThat(
            CredentialsChain.Profile(Environment.EMPTY)(),
            absent()
        )
    }

    @Test
    fun `credentials are cached`() {
        profileFile.toFile().writeText("""
            [default]
            aws_access_key_id = key123
            aws_secret_access_key = secret123
        """)

        val expected = AwsCredentials("key123", "secret123")
        val chain = CredentialsChain.Profile(env)

        assertThat(chain.invoke(), equalTo(expected))

        profileFile.toFile().writeText("""
            [default]
            aws_access_key_id = key1456
            aws_secret_access_key = secret456
        """)

        assertThat(chain.invoke(), equalTo(expected))
    }
}
