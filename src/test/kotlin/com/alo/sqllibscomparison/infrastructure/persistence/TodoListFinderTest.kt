package com.alo.sqllibscomparison.infrastructure.persistence

import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import com.alo.sqllibscomparison.domain.TodoListId
import com.alo.sqllibscomparison.domain.boundary.Pagination
import com.alo.sqllibscomparison.domain.boundary.SearchBy
import com.alo.sqllibscomparison.domain.boundary.TodoListFinder
import com.alo.sqllibscomparison.infrastructure.persistence.fixtures.TodoLists
import org.junit.jupiter.api.Test
import java.util.UUID

abstract class TodoListFinderTest : PostgreSQLIntegrationTest(), WithTestTransaction {

    abstract val todoListFinder: TodoListFinder


    @Test
    fun `should get a single to-do list`() = withTestTransaction {
        assertThat(todoListFinder.get(TodoListId(value = UUID.fromString("4db3b6ae-3257-46b6-b3c3-d8b91239632a"))))
            .isEqualTo(TodoLists.weekendTodoList)
    }

    @Test
    fun `should return a nullable getting a non-existent to-do list`() = withTestTransaction {
        assertThat(todoListFinder.get(TodoListId(value = UUID.fromString("f8a27cde-4a93-4247-b0e5-77f622c37a07"))))
            .isNull()
    }

    @Test
    fun `should list all to-do lists`() = withTestTransaction {
        assertThat(todoListFinder.listAll())
            .containsExactly(
                    TodoLists.weekendTodoList,
                    TodoLists.pizzaFridayTodoList,
                    TodoLists.supermanTodoList,
                    TodoLists.shoppingList,
                    TodoLists.todayTodoList
            )
    }

    @Test
    fun `should paginate when searching to-do lists`() = withTestTransaction {
        assertThat(todoListFinder.search(pagination = Pagination(1,3)))
            .containsExactly(
                    TodoLists.pizzaFridayTodoList,
                    TodoLists.supermanTodoList,
                    TodoLists.shoppingList
            )
    }

    @Test
    fun `should search by name to-do lists`() = withTestTransaction {
        assertThat(todoListFinder.search(searchBy = SearchBy(name = "list")))
            .containsExactly(
                TodoLists.supermanTodoList,
                TodoLists.shoppingList
            )
    }

    @Test
    fun `should find todo lists with pending tasks to be finished`() = withTestTransaction {
        assertThat(todoListFinder.findNotCompleted())
            .containsExactly(
                TodoLists.pizzaFridayTodoList,
                TodoLists.supermanTodoList,
                TodoLists.todayTodoList
            )
    }
}
