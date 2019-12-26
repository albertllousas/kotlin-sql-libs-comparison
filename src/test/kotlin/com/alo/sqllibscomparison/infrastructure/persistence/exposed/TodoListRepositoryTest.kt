package com.alo.sqllibscomparison.infrastructure.persistence.exposed

import com.alo.sqllibscomparison.infrastructure.persistence.TodoListFinderTest
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.BeforeEach

class TodoListRepositoryTest: TodoListFinderTest() {
    override val todoListFinder = TodoListRepository()

    @BeforeEach
    fun `connect to database`() {
        Database.connect(
            url = postgresqlContainer.jdbcUrl,
            driver = "org.postgresql.Driver",
            user = "user",
            password = "password"
        )
    }

    override fun withTestTransaction(testBlock: () -> Unit) {

        transaction {
            addLogger(StdOutSqlLogger)
            testBlock() }
    }

}
