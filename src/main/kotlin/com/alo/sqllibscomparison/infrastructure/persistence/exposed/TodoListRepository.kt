package com.alo.sqllibscomparison.infrastructure.persistence.exposed

import com.alo.sqllibscomparison.domain.TodoList
import com.alo.sqllibscomparison.domain.TodoListId
import com.alo.sqllibscomparison.domain.boundary.Pagination
import com.alo.sqllibscomparison.domain.boundary.SearchBy
import com.alo.sqllibscomparison.domain.boundary.TodoListFinder
import com.alo.sqllibscomparison.infrastructure.persistence.exposed.TodoListDomainMappers.toMultipleTodoLists
import com.alo.sqllibscomparison.infrastructure.persistence.exposed.TodoListDomainMappers.toSingleTodoList
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.VarCharColumnType
import org.jetbrains.exposed.sql.andWhere
import org.jetbrains.exposed.sql.castTo
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll

class TodoListRepository : TodoListFinder {

    override fun listAll(): List<TodoList> =
        TodoLists
            .leftJoin(Tasks)
            .selectAll()
            .orderBy(TodoLists.created, SortOrder.DESC)
            .toList()
            .let(::toMultipleTodoLists)

    override fun get(id: TodoListId): TodoList? =
        TodoLists
            .leftJoin(Tasks)
            .select { TodoLists.id eq id.value }
            .toList()
            .let { if (it.isNotEmpty()) toSingleTodoList(it) else null }

    override fun findNotCompleted(): List<TodoList> {
        val notCompleted = TodoLists
            .leftJoin(Tasks)
            .slice(TodoLists.id)
            .select { Tasks.status.castTo<String>(VarCharColumnType()) eq TaskStatus.TODO.name }
        return TodoLists
            .leftJoin(Tasks)
            .select { TodoLists.id inSubQuery notCompleted }
            .orderBy(TodoLists.created, SortOrder.DESC)
            .distinct()
            .toList()
            .let(::toMultipleTodoLists)
    }

    override fun search(
        searchBy: SearchBy?,
        pagination: Pagination?
    ): List<TodoList> {
        val resultsWithoutTasks = TodoLists
            .slice(TodoLists.id)
            .selectAll()
            .let { query -> searchBy?.let { query.andWhere { TodoLists.name like "%${it.name}%" } } ?: query }
            .let { query -> pagination?.let { query.limit(n = it.limit, offset = it.start) } ?: query }
            .orderBy(TodoLists.created, SortOrder.DESC)
        return TodoLists
            .leftJoin(Tasks)
            .select { TodoLists.id inSubQuery resultsWithoutTasks }
            .orderBy(TodoLists.created, SortOrder.DESC)
            .toList()
            .let(::toMultipleTodoLists)
    }

}
