package com.alo.sqllibscomparison.infrastructure.persistence.exposed

import com.alo.sqllibscomparison.domain.TodoList
import com.alo.sqllibscomparison.domain.TodoListId
import com.alo.sqllibscomparison.domain.boundary.Pagination
import com.alo.sqllibscomparison.domain.boundary.SearchBy
import com.alo.sqllibscomparison.domain.boundary.TodoListFinder
import com.alo.sqllibscomparison.infrastructure.persistence.exposed.TodoListDomainMapper.toMultipleTodoLists
import com.alo.sqllibscomparison.infrastructure.persistence.exposed.TodoListDomainMapper.toSingleTodoList
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.VarCharColumnType
import org.jetbrains.exposed.sql.andWhere
import org.jetbrains.exposed.sql.castTo
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

class TodoListRepository : TodoListFinder {

    override fun listAll(): List<TodoList> = transaction {
        TodoLists
            .leftJoin(Tasks)
            .selectAll()
            .orderBy(TodoLists.created, SortOrder.DESC)
            .toList()
            .let(::toMultipleTodoLists)
    }

    override fun get(id: TodoListId): TodoList? = transaction {
        TodoLists
            .leftJoin(Tasks)
            .select(where = { TodoLists.id eq id.value })
            .toList()
            .let { if (it.isNotEmpty()) toSingleTodoList(it) else null }
    }

    override fun findNotCompleted(): List<TodoList> = transaction {
        val notCompleted = TodoLists
            .leftJoin(Tasks)
            .slice(TodoLists.id)
            .select { Tasks.status.castTo<String>(VarCharColumnType()) eq TaskStatus.TODO.name }
        TodoLists
            .leftJoin(Tasks)
            .select(where = { TodoLists.id inSubQuery notCompleted })
            .orderBy(TodoLists.created, SortOrder.DESC)
            .toList()
            .let(::toMultipleTodoLists)
    }

    override fun search(
        searchBy: SearchBy?,
        pagination: Pagination?
    ): List<TodoList> = transaction {
        val todoListIds = TodoLists
            .slice(TodoLists.id)
            .selectAll()
            .let { query -> searchBy?.let { query.andWhere { TodoLists.name like "%${it.name}%" } } ?: query }
            .let { query -> pagination?.let { query.limit(n = it.limit, offset = it.start) } ?: query }
            .orderBy(TodoLists.created, SortOrder.DESC)
        TodoLists
            .leftJoin(Tasks)
            .select(where = { TodoLists.id inSubQuery todoListIds })
            .orderBy(TodoLists.created, SortOrder.DESC)
            .toList()
            .let(::toMultipleTodoLists)
    }

}
