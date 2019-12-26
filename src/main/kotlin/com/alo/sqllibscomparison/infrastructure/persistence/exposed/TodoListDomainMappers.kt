package com.alo.sqllibscomparison.infrastructure.persistence.exposed

import com.alo.sqllibscomparison.domain.Status
import com.alo.sqllibscomparison.domain.Task
import com.alo.sqllibscomparison.domain.TodoList
import com.alo.sqllibscomparison.domain.TodoListId
import org.jetbrains.exposed.sql.ResultRow
import java.util.UUID

object TodoListDomainMappers {
    private val groupByTodoList: (ResultRow) -> UUID = { row -> row[TodoLists.id] }

    fun toMultipleTodoLists(rows: List<ResultRow>) =
        rows.groupBy(groupByTodoList)
            .mapValues { (_, groupedRows) -> toSingleTodoList(groupedRows) }
            .values
            .toList()

    fun toSingleTodoList(rows: List<ResultRow>): TodoList {
        val sorted = rows.sortedBy { it[Tasks.position] }
        val firstRow = sorted.first()
        val tasks = firstRow.getOrNull(Tasks.id)?.let { sorted.map { row -> row.toTask() } } ?: emptyList()
        return firstRow.toTodoList().copy(tasks = tasks)
    }


    private fun ResultRow.toTodoList() = TodoList(
        id = TodoListId(this[TodoLists.id]),
        name = this[TodoLists.name],
        tasks = emptyList()
    )

    private fun ResultRow.toTask() = Task(
        name = this[Tasks.name],
        status = when (this[Tasks.status]) {
            TaskStatus.TODO -> Status.TODO
            TaskStatus.DONE -> Status.DONE
        }
    )

}