package com.alo.sqllibscomparison.infrastructure.persistence.jooq

import com.alo.sqllibscomparison.domain.Status
import com.alo.sqllibscomparison.domain.Task
import com.alo.sqllibscomparison.domain.TodoList
import com.alo.sqllibscomparison.domain.TodoListId
import com.alo.sqllibscomparison.infrastructure.jooq.generated.enums.TaskStatus.*
import com.alo.sqllibscomparison.infrastructure.jooq.generated.tables.records.TaskRecord
import com.alo.sqllibscomparison.infrastructure.jooq.generated.tables.records.TodoListRecord
import com.alo.sqllibscomparison.infrastructure.persistence.exposed.TodoLists

object TodoListDomainMapper {
    fun toTodoList(
        todoListRecord: TodoListRecord,
        taskRecords: List<TaskRecord>
    ): TodoList = todoListRecord.toTodoList().copy(tasks = taskRecords.sortedBy { it.position }.map { it.toTask() })

    private fun TodoListRecord.toTodoList() = TodoList(
        id = TodoListId(this.id),
        name = this.name,
        tasks = emptyList()
    )

    private fun TaskRecord.toTask() = Task(
        name = this.name,
        status = when (this.status) {
            TODO -> Status.TODO
            DONE -> Status.DONE
        }
    )
}
