package org.http4k.connect.storage

import org.http4k.connect.assumeDockerDaemonRunning
import org.http4k.core.Credentials
import org.http4k.format.PostgresJackson
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.BeforeEach
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName

@Testcontainers
class PostgresStorageTest : StorageContract() {

    private val name = "foobar"

    init {
        assumeDockerDaemonRunning()
    }

    private val credentials = Credentials("postgres", "password")

    @Container
    val postgres = PostgreSQLContainer<PostgreSQLContainer<*>>(DockerImageName.parse("postgres"))
        .withUsername(credentials.user)
        .withPassword(credentials.password)

    @BeforeEach
    fun before() {
        val db = Database.connect("jdbc:postgresql://${postgres.host}:${postgres.firstMappedPort}", user = credentials.user, password = credentials.password)

        transaction(db){
            SchemaUtils.createDatabase(name)
        }
    }

    override val storage: Storage<String> by lazy {
        Storage.PostgresJackson(name, credentials, "jdbc:postgresql://${postgres.host}:${postgres.firstMappedPort}/$name")
    }
}
