package org.http4k.connect.amazon.core.credentials

import com.natpryce.hamkrest.absent
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.http4k.aws.AwsCredentials
import org.http4k.cloudnative.env.Environment
import org.http4k.connect.amazon.AWS_CREDENTIAL_PROFILES_FILE
import org.http4k.connect.amazon.AWS_PROFILE
import org.http4k.connect.amazon.core.model.ProfileName
import org.http4k.connect.amazon.defaultCredentialsProfilesFile
import org.http4k.core.with
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Path

class ProfileCredentialsChainTest {

    private val createdFiles = mutableListOf<Path>()

    private val sampleCredentialsIni = """
        [default]
        aws_access_key_id = key123
        aws_secret_access_key = secret123
        
        [dev]
        aws_access_key_id = key456
        aws_secret_access_key = secret456
    """

    @AfterEach
    fun cleanupFiles() {
        for (file in createdFiles) {
            file.toFile().delete()
        }
    }

    @Test
    fun `default profile in default file`() {
        defaultCredentialsProfilesFile.write(sampleCredentialsIni)

        assertThat(
            CredentialsChain.Profile(Environment.EMPTY)(),
            equalTo(AwsCredentials("key123", "secret123"))
        )
    }

    @Test
    fun `custom profile in custom file`() {
        val file = Files.createTempFile("credentials", "ini")
        file.write(sampleCredentialsIni)

        val env = Environment.EMPTY
            .with(AWS_PROFILE of ProfileName.of("dev"))
            .with(AWS_CREDENTIAL_PROFILES_FILE of file)

        assertThat(
            CredentialsChain.Profile(env)(),
            equalTo(AwsCredentials("key456", "secret456"))
        )
    }

    @Test
    fun `file has no default profile`() {
        val file = Files.createTempFile("credentials", "ini")
        file.write("""
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
        val file = Files.createTempFile("credentials", "ini")
        file.write(sampleCredentialsIni)

        val env = Environment.EMPTY
            .with(AWS_PROFILE of ProfileName.of("prod"))

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

    private fun Path.write(text: String) {
        createdFiles.add(this)
        toFile().writeText(text)
    }
}
