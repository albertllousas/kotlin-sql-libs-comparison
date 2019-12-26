package com.alo.sqllibscomparison.infrastructure.persistence.exposed

import com.github.javafaker.Faker
import org.jetbrains.exposed.sql.ResultRow
import java.util.UUID

val faker = Faker()

fun buildTodoListResultRow(
    todoListId: UUID,
    todoListName: String = faker.funnyName().name(),
    taskId: UUID = UUID.randomUUID(),
    taskName: String = faker.funnyName().name(),
    status: TaskStatus = faker.options().option(TaskStatus::class.java)
) = ResultRow(
    mapOf(
        Pair(TodoLists.id, 0),
        Pair(TodoLists.name, 1),
        Pair(Tasks.id, 2),
        Pair(Tasks.name, 3),
        Pair(Tasks.status, 4)
    )
).apply {
    set(TodoLists.id, todoListId)
    set(TodoLists.name, todoListName)
    set(Tasks.id, taskId)
    set(Tasks.name, taskName)
    set(Tasks.status, status.name)
}
