package com.alo.sqllibscomparison.infrastructure.persistence.jooq

import com.alo.sqllibscomparison.infrastructure.persistence.TodoListFinderTest
import org.jooq.impl.DSL


class TodoListRepositoryTest: TodoListFinderTest() {

    override val todoListFinder by lazy {
        TodoListRepository( DSL.using(postgresqlContainer.jdbcUrl, "user", "password"))
    }

    override fun withTestTransaction(testBlock: () -> Unit) {
        testBlock()
    }
}
