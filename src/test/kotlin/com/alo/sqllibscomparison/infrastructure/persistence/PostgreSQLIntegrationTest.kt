package com.alo.sqllibscomparison.infrastructure.persistence

import org.jetbrains.exposed.sql.Database
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.testcontainers.containers.PostgreSQLContainer

abstract class PostgreSQLIntegrationTest {

    protected val postgresqlContainer = KtPostgreSQLContainer()
        .withUsername("user")
        .withPassword("password")
        .withInitScript("todo-list-database.sql")

    @BeforeEach
    fun `init`() {
        postgresqlContainer.start()
    }

    @AfterEach
    fun `tear down`() {
        postgresqlContainer.stop()
    }
}