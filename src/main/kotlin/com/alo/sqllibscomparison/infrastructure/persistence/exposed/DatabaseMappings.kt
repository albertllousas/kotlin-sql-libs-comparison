package com.alo.sqllibscomparison.infrastructure.persistence.exposed

import org.jetbrains.exposed.sql.Table

enum class TaskStatus { TODO, DONE }

object Tasks : Table("TASK") {
    val id = uuid("id").primaryKey()
    val name = text("name")
    val todoListId = (uuid("todo_list_id") references TodoLists.id)
    val status = enumerationByName("status", 10, TaskStatus::class)
    val position = integer("position")
}

object TodoLists : Table("TODO_LIST") {
    val id = uuid("id").primaryKey()
    val name = text("name")
    val created = date("created")
    val updated = date("updated")
}
