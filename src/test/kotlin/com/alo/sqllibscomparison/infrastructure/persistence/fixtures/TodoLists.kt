package com.alo.sqllibscomparison.infrastructure.persistence.fixtures

import com.alo.sqllibscomparison.domain.Status
import com.alo.sqllibscomparison.domain.Status.DONE
import com.alo.sqllibscomparison.domain.Status.TODO
import com.alo.sqllibscomparison.domain.Task
import com.alo.sqllibscomparison.domain.TodoList
import com.alo.sqllibscomparison.domain.TodoListId
import com.github.javafaker.Faker
import java.util.UUID
import kotlin.random.Random

object TodoLists {

    private val faker = Faker()

    fun buildTodoList(
        id: UUID = UUID.randomUUID(),
        name: String = faker.funnyName().name(),
        tasks: List<Task> = (0..Random.nextInt(0, 5)).map { buildTask() }
    ): TodoList = TodoList(TodoListId(id), name, tasks)

    fun buildTask(
        name: String = faker.funnyName().name(),
        status: Status = faker.options().option(Status::class.java)
    ): Task = Task(name, status)

    val supermanTodoList = buildTodoList(
        id = UUID.fromString("6ff7334f-b270-460b-a082-8854e567c38c"),
        name = "Superman list",
        tasks = listOf(buildTask(name = "Save the world", status = TODO))
    )
    val weekendTodoList = buildTodoList(
        id = UUID.fromString("4db3b6ae-3257-46b6-b3c3-d8b91239632a"),
        name = "Weekend",
        tasks = listOf(buildTask(name = "Get drunk", status = DONE))
    )
    val pizzaFridayTodoList = buildTodoList(
        id = UUID.fromString("202dba03-0505-4192-a374-503cab38a94b"),
        name = "Pizza friday",
        tasks = listOf(
            buildTask(name = "Pineapple", status = Status.TODO),
            buildTask(name = "Tomato", status = Status.TODO),
            buildTask(name = "Mozzarella", status = Status.TODO)
        )
    )
    val shoppingList = buildTodoList(
        id = UUID.fromString("fed97520-f3cb-4ff0-aec7-ce6596768579"),
        name = "Shopping list",
        tasks = listOf(
            buildTask(name = "Human food", status = Status.DONE),
            buildTask(name = "Cat food", status = Status.DONE)
        )
    )
    val todayTodoList = buildTodoList(
        id = UUID.fromString("d3787c68-0d5b-474e-9a25-f5a0d079fa4b"),
        name = "Today",
        tasks = listOf(
            buildTask(name = "Get up", status = Status.DONE),
            buildTask(name = "Survive", status = Status.TODO),
            buildTask(name = "Go to bed", status = Status.TODO)
        )
    )

}