package com.alo.sqllibscomparison.infrastructure.persistence.jooq

import com.alo.sqllibscomparison.domain.TodoList
import com.alo.sqllibscomparison.domain.TodoListId
import com.alo.sqllibscomparison.domain.boundary.Pagination
import com.alo.sqllibscomparison.domain.boundary.SearchBy
import com.alo.sqllibscomparison.domain.boundary.TodoListFinder
import com.alo.sqllibscomparison.infrastructure.jooq.generated.Tables.TASK
import com.alo.sqllibscomparison.infrastructure.jooq.generated.Tables.TODO_LIST
import com.alo.sqllibscomparison.infrastructure.jooq.generated.enums.TaskStatus
import com.alo.sqllibscomparison.infrastructure.persistence.exposed.TodoLists
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import org.jetbrains.exposed.sql.andWhere
import org.jooq.DSLContext

class TodoListRepository(private val jooq: DSLContext) : TodoListFinder {

    override fun listAll(): List<TodoList> =
        jooq.select()
            .from(TODO_LIST)
            .leftJoin(TASK)
            .on(TASK.TODO_LIST_ID.eq(TODO_LIST.ID))
            .orderBy(TODO_LIST.CREATED.desc())
            .fetchGroups(TODO_LIST)
            .mapValues { it.value.into(TASK).toList() }
            .map { (todoListRecord, taskRecords) -> TodoListDomainMapper.toTodoList(todoListRecord, taskRecords) }

    override fun search(
        searchBy: SearchBy?,
        pagination: Pagination?
    ): List<TodoList> {
        val todoListIds = jooq
            .select(TODO_LIST.ID)
            .from(TODO_LIST)
            .let { query -> searchBy?.let { query.where(TODO_LIST.NAME.like ("%${it.name}%")) } ?: query }
            .orderBy(TODO_LIST.CREATED.desc())
            .let { query -> pagination?.let { query.limit(it.limit).offset(it.start) } ?: query }

        return jooq.select()
            .from(TODO_LIST)
            .leftJoin(TASK)
            .on(TASK.TODO_LIST_ID.eq(TODO_LIST.ID))
            .where(TASK.TODO_LIST_ID.`in`(todoListIds))
            .orderBy(TODO_LIST.CREATED.desc())
            .fetchGroups(TODO_LIST)
            .mapValues { it.value.into(TASK).toList() }
            .map { (todoListRecord, taskRecords) -> TodoListDomainMapper.toTodoList(todoListRecord, taskRecords) }
    }

    override fun findNotCompleted(): List<TodoList> {
        val notCompleted = jooq
            .select(TODO_LIST.ID)
            .distinctOn(TODO_LIST.ID)
            .from(TODO_LIST)
            .leftJoin(TASK)
            .on(TASK.TODO_LIST_ID.eq(TODO_LIST.ID))
            .where(TASK.STATUS.eq(TaskStatus.TODO))

        return jooq.select()
            .from(TODO_LIST)
            .leftJoin(TASK)
            .on(TASK.TODO_LIST_ID.eq(TODO_LIST.ID))
            .where(TASK.TODO_LIST_ID.`in`(notCompleted))
            .orderBy(TODO_LIST.CREATED.desc())
            .fetchGroups(TODO_LIST)
            .mapValues { it.value.into(TASK).toList() }
            .map { (todoListRecord, taskRecords) -> TodoListDomainMapper.toTodoList(todoListRecord, taskRecords) }
    }

    override fun get(id: TodoListId): TodoList? =
        jooq.select()
            .from(TODO_LIST)
            .leftJoin(TASK)
            .on(TASK.TODO_LIST_ID.eq(TODO_LIST.ID))
            .where(TODO_LIST.ID.eq(id.value))
            .fetchGroups(TODO_LIST)
            .mapValues { it.value.into(TASK).toList() }
            .map { (todoListRecord, taskRecords) -> TodoListDomainMapper.toTodoList(todoListRecord, taskRecords) }
            .let { if (it.isNotEmpty()) it.first() else null }

}
