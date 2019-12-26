package com.alo.sqllibscomparison.infrastructure.persistence.jooq

import com.alo.sqllibscomparison.domain.TodoList
import com.alo.sqllibscomparison.domain.TodoListId
import com.alo.sqllibscomparison.domain.boundary.Pagination
import com.alo.sqllibscomparison.domain.boundary.SearchBy
import com.alo.sqllibscomparison.domain.boundary.TodoListFinder
import org.jooq.DSLContext

class JooqTodoListRepository(private val jooq: DSLContext) : TodoListFinder {
    override fun listAll(): List<TodoList> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun search(
        searchBy: SearchBy?,
        pagination: Pagination?
    ): List<TodoList> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun findNotCompleted(): List<TodoList> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun get(id: TodoListId): TodoList? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


}
