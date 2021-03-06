# Kotlin sql libs comparison

- [Kotlin sql libs comparison](#kotlin-sql-libs-comparison)
  * [Description](#description)
    + [Challenge](#challenge)
  * [DSL vs ORM](#dsl-vs-orm)
    + [Approach](#approach)
    + [Project Stack](#project-stack)
  * [Project Structure](#project-structure)
  * [Developing](#developing)
    + [Running](#running)
    + [Collaborating](#collaborating)
  * [Libs Comparison](#libs-comparison)
    + [Database mapping](#database-mapping)
      - [Exposed](#exposed)
      - [Jooq](#jooq)
    + [DSL](#dsl)
      - [Exposed](#exposed-1)
      - [Jooq](#jooq-1)
    + [Mapping back results: Type safety](#mapping-back-results--type-safety)
      - [Exposed](#exposed-2)
      - [Jooq](#jooq-2)
    + [Querying](#querying)
      - [One to Many relationships](#one-to-many-relationships)
        * [Exposed](#exposed-3)
        * [Jooq](#jooq-3)
      - [Sub-querying](#sub-querying)
        * [Exposed](#exposed-4)
        * [Jooq](#jooq-4)
      - [Dynamic querying](#dynamic-querying)
        * [Exposed](#exposed-5)
        * [Jooq](#jooq-5)

## Description

The idea of this project is to compare and see advantages and disadvantages of different SQL libs when they are used
 with kotlin with a simple problem as a context.

The problem to model is the typical `TodoList`, where you have a TODO list and it is composed with multiple tasks.

Here we have the **DB model** that we are going to use in the whole project.

<p align="center">
  <img src="img/database-diagram.png">
</p>

And this is the **domain** that we are going to use:

<p align="center">
  <img src="img/class-diagram.png">
</p>

DB model and domain are not the same, they diverge.

The [domain](./src/main/kotlin/com/alo/sqllibscomparison/domain/TodoList.kt) in kotlin is pretty simple:
```kotlin
data class TodoList(val id: TodoListId, val name: String, val tasks: List<Task>)

inline class TodoListId(val value: UUID)

data class Task(val name: String, val status: Status)

enum class Status { TODO, DONE }
```

With this simple problem we will tackle different problems that you always face when you are have to integrate with a
 SQL library like, how easy is to use, SQL generation, model mapping, maintainability, testability ... 

The idea is to have different implementations of the same repository in kotlin to access to this aggregate root
 with two different libraries:
* [Jooq](https://www.jooq.org/)
* [Exposed](https://github.com/JetBrains/Exposed)
* Feel free to add your favourite framework here. 

### Challenge

To see how flexible these libs are, let's imagine some use-cases that will force the implementations to handle not
 just simple queries:
 
* Since we will always access to an aggregate root, all queries will imply **one-to-many** joins; it means dealing with
 multiple rows and grouping, not just simple mappings from a row to a dto. 
* Find all not completed todo lists : Sub-querying
* Search and paginate todo lists: Conditional and dynamic querying

To make it easy and consistent, implementations should implement [interfaces](./src/main/kotlin/com/alo/sqllibscomparison/domain/boundary/):
```kotlin
interface TodoListFinder {
    fun listAll() : List<TodoList>
    fun search(searchBy: SearchBy? = null, pagination: Pagination? = null) : List<TodoList>
    fun findNotCompleted(): List<TodoList>
    fun get(id: TodoListId): TodoList?
}

data class SearchBy(val name: String)
data class Pagination(val start:Int = 0, val limit:Int=20)
```
 
## DSL vs ORM

Almost all SQL libraries offer an ORM version. We are not going to use the any of the libraries as an [ORM](https://en.wikipedia.org/wiki/Object-relational_mapping), **We want to use and love SQL**. 

Personal opinion about ORMs after being working with them many years:  "_I think ORMs are an anti-pattern and if you
 don't use them carefully your code will be prone to errors, bad design and bad performance._".
 
Why I consider ORMs an anti-pattern:
- SQL and OOP are different, they are simply different worlds, the first is about joins and the seconds about
 relationships.
- ORMs are not a real SQL abstractions: You want to get away from SQL but at the same time you have to be aware of what
 are they doing under the hood.
- They are not simple at all, they come with code generation and magic, your code will become dirty and tricky when
 you have to do complex queries (80% of projects)
- Loose totally the control of the generated SQL:
    - Prone to performance errors
    - Mysterious SQL code generation
    - Death by a thousand queries
- Database specific features or complex SQL features are not usually covered by ORMs
- Too easy to couple your domain to your database model
- The last reason, and maybe the most important one, with a good separation of concerns and following hexagonal
, onion or clean architectures, the code related to DB, should belong to infrastructure layer, so why would you want
 get rid of SQL in the implementation layer?

So, my question would be, why you want to use an ORM with all the risks listed above?

But, to be honest, JDBC directly is a bit painful:
- We don't want to write SQL directly:
    - We don't want to write SQL as strings.
    - Avoid dynamic queries generation with plain SQL   
    - Update strings is not scalable and maintainable at all.
- We don't want to deal with jdbc ResultSets, we want an easy way to map our results back to the domain, and again, if
 they could have some type of type safety it would also be great.
 
### Approach
**In a nutshell, our approach would like to use the frameworks as a :**
- Lightweight layer on top of of jdbc
- DSL, it means writing code that is similar to SQL syntax to access the DB, we love SQL but we want some help and
 type safety at compile time :) 
- Domain mapping facilitators
- Database first, we want to know what SQL we are generating!

### Project Stack

* Language: Kotlin
* Build tool: Gradle
* DB: PostgreSQL
* Test:
    * TestContainers
    * JUnit5
    * AssertK

## Project Structure
```bash
|-- main
|   |-- java
|   |   `-- com
|   |       `-- alo
|   |           `-- sqllibscomparison
|   |               `-- infrastructure
|   |                   `-- jooq
|   |                       `-- generated
|   |                           |-- *.java // JOOQ DB MAPPINGS (AUTO GENERATED CODE)
|   |-- kotlin
|      `-- com
|          `-- alo
|              `-- sqllibscomparison
|                  |-- domain
|                  |   |-- TodoList.kt // DOMAIN
|                  |   `-- boundary
|                  |       `-- TodoListFinder.kt // CONTRACTS
|                  `-- infrastructure
|                      `-- persistence
|                          |-- exposed
|                          |   |-- DatabaseMappings.kt  // EXPOSED DB MAPPINGS
|                          |   |-- TodoListDomainMapper.kt // TRANSLATIONS FROM EXPOSED RESULTS TO OUR DOMAIN 
|                          |   `-- TodoListRepository.kt  // EXPOSED TODO-LIST REPO
|                          `-- jooq
|                              |-- TodoListDomainMapper.kt // TRANSLATIONS FROM JOOQ RESULTS TO OUR DOMAIN  
|                              `-- TodoListRepository.kt  // JOOQ TODO-LIST REPO 
`-- test
    |-- kotlin
       `-- com
           `-- alo
               `-- sqllibscomparison
                   `-- infrastructure
                       `-- persistence
                           |-- *.kt // main test classes
                           |-- exposed
                           |   `-- TodoListRepositoryTest.kt // Exposed tests
                           `-- jooq
                               |-- CodeGen.kt // JOOQ code generator main
                               `-- TodoListRepositoryTest.kt // JOOQ tests

```
## Developing

### Running

All the project is based on tests, so to run it just:

```shell script
./gradlew test
``` 
### Collaborating
If you want to add your fancy SQL lib.
1. Add your impl to: 
[`/src/main/kotlin/com/alo/sqllibscomparison/infrastructure/persistence`](/src/main/kotlin/com/alo/sqllibscomparison/infrastructure/persistence)
2. Implement the interfaces declared in:
[`/src/main/kotlin/com/alo/sqllibscomparison/domain/boundary`](/src/main/kotlin/com/alo/sqllibscomparison/domain/boundary)
3. Add your test (there is no need to implement test, just extend this class):
[`/src/test/kotlin/com/alo/sqllibscomparison/infrastructure/persistence/TodoListFinderTest.kt`](/src/test/kotlin/com/alo/sqllibscomparison/infrastructure/persistence/TodoListFinderTest.kt)
4. Enrich this readme with your thoughts/findings
5. Create a PR


## Libs Comparison

### Database mapping
Almost libraries that access to a database imply mapping the schema somehow, some use annotations, another simple dtos
and other complex apis. Let's see how the frameworks in comparison handle that. 
#### Exposed

In Exposed we don't work with raw SQL strings. Instead, we map tables, columns, keys, relationships, etc
... using a high-level DSL. The DSL (Domain Specific Language) API of Exposed, is similar to actual SQL statements with type safety that Kotlin offers. 
We will need to [mimic](./src/main/kotlin/com/alo/sqllibscomparison/infrastructure/persistence/exposed/DatabaseMappings.kt) our DB in our code to use the DSL afterwards:
```kotlin
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
```
The API provided by Exposed is easy to use, allowing to map our DB pretty straight forward, just mapping tables
 directly. 

*Note:* Exposed is not mapping the database automatically, so we will need to do the changes manually when we change
 the DB, if not the code will be not consistent.

#### Jooq

There are many ways to work with jooq, but in our case we use the code generation. jOOQ's code generator takes your
 database schema and reverse-engineers it into a set of Java classes modelling tables, records, sequences, POJOs
 , DAOs, stored procedures, user-defined types and many more.

There are several maven and gradle plugins to generate the code integrated with the project lifecycle, but in our
 case we will use programmatic generator configuration, just do in it by ourselves.
 [CodeGen](./src/main/kotlin/com/alo/sqllibscomparison/infrastructure/persistence/jooq/CodeGen.kt)
 
 To regenerate all the mappings (we have to add extra code in gradle to run it):
 ```shell script
./gradlew JooqCodeGen
```

 The generated code is in java, not kotlin, and as you can see it reflects exactly all the database schema, it maps
  everything from tables to records, even indexes:
[Java generated code](./src/main/java/com/alo/sqllibscomparison/infrastructure/jooq/generated) 

*Note*: Now all the mappings are auto-generated by Jooq, but they are generated in java and we have to add extra code in
 our build lifecycle.
 
### DSL
In order to interact with our data stores we will need in one way or another to call the libs APIs to access or modify
 our data, it means that under the hood, all the frameworks will translate this calls to real SQL.
Almost all of them provide [DSL](https://martinfowler.com/dsl.html)'s to facilitate this translation.

SQL have an extended syntax for accessing and manipulating databases. In the next section we are going to center in
 just one example to see how natural it feels and how close it is to the real SQL.
#### Exposed
The DSL (Domain Specific Language) API of Exposed, is similar to actual SQL statements with type safety that Kotlin offers.
For example, listing all todo-list, in plain sql:
```postgresql
SELECT todo_list.id, todo_list."name", todo_list.created, todo_list.updated, task.id, task."name", task.todo_list_id, task.status, task."position" 
FROM todo_list 
LEFT JOIN task ON todo_list.id = task.todo_list_id 
ORDER BY todo_list.created DESC
```
With exposed this query get translated to:
```kotlin
TodoLists
    .leftJoin(Tasks)
    .selectAll()
    .orderBy(TodoLists.created, SortOrder.DESC)
```
Just with [schema mappings](./src/main/kotlin/com/alo/sqllibscomparison/infrastructure/persistence/exposed/DatabaseMappings.kt) we can use a simple and powerful API to easily access to our data.
 
#### Jooq
SQL building is the main feature of jOOQ, after run the code generator we could use Jooq API to in many ways to query
 our data.
Again, listing all todo-list, in plain sql:
```postgresql
SELECT todo_list.id, todo_list."name", todo_list.created, todo_list.updated, task.id, task."name", task.todo_list_id, task.status, task."position" 
FROM todo_list 
LEFT JOIN task ON todo_list.id = task.todo_list_id 
ORDER BY todo_list.created DESC
```
With Jooq this query get translated to:
```kotlin
 jooq.select()
    .from(TODO_LIST)
    .leftJoin(TASK)
    .on(TASK.TODO_LIST_ID.eq(TODO_LIST.ID))
    .orderBy(TODO_LIST.CREATED.desc())
```
The Jooq API is extremely powerful, with a lot of methods available to do complex queries, take a look on [their docs](https://www.jooq.org/doc/3.12/manual/sql-building/), they are huge with infinite options.

*Note*:Since Jooq is built in java, we can use it, but we can not use all the power of kotlin on it, like named params.

### Mapping back results: Type safety
The other side of querying is parsing the results, and to do that we will need always to map them back to your domain or
 to a temporal DTO layer.
 
Usually frameworks provide abstractions of [ResultSets](https://docs.oracle.com/javase/8/docs/api/java/sql/ResultSet.html) 
to iterate over them, mapper apis to map directly to DTOs or even sometimes integration with third parties to do that.
 
In our case we are going to check how type-safe they are in terms of compile time.

#### Exposed
Queries in Exposed return `Query` objects that inherit Iterable<`ResultRow`>, we can see it as a map keyed by column
 or a projection of a real database row.
So, after a query we would have to map the resulting single or multiple `ResultRow` to our domain:

In our case we only need to define two maps with two simple extension functions:

1. from `ResultRow` to `TodoList`:
```kotlin
private fun ResultRow.toTodoList() = TodoList(
        id = TodoListId(this[TodoLists.id]),
        name = this[TodoLists.name],
        tasks = emptyList()
    )
```
2. from `ResultRow` to `Task`:
```kotlin
private fun ResultRow.toTask() = Task(
    name = this[Tasks.name],
    status = when (this[Tasks.status]) {
        TaskStatus.TODO -> Status.TODO
        TaskStatus.DONE -> Status.DONE
    }
)
```
[Source](/src/main/kotlin/com/alo/sqllibscomparison/infrastructure/persistence/exposed/TodoListDomainMapper.kt).

As we can see, it is easy to access to them, as we said, `ResultRow` is like a Map with keys as columns, and we can
 access to the values just using the columns declared in our schema definition.
 
*Important note:* Even thought it is super easy to access to the results, there is **no type-safety at all**, we just
 access to a some kind of `Map`; so, if we make an error we would see it at runtime.
 
#### Jooq 
As all the features in Jooq, there are multiple ways to [fetch data](https://www.jooq.org/doc/latest/manual/sql-execution/fetching/) and map them back 
to our domain, but in this example we are using [strongly typed records](https://www.jooq.org/doc/3.12/manual/sql-execution/fetching/record-vs-tablerecord/)
 since we are using code-generation and not as an ORM.

Again, we only need to define two extension functions:

1. from [`TodoListRecord`](/src/main/java/com/alo/sqllibscomparison/infrastructure/jooq/generated/tables/records/TodoListRecord.java) to [`TodoList`](/src/main/kotlin/com/alo/sqllibscomparison/domain/TodoList.kt#L5):
```kotlin
private fun TodoListRecord.toTodoList() = TodoList(
        id = TodoListId(this.id),
        name = this.name,
        tasks = emptyList()
    )
```
2. from [`TaskRecord`](/src/main/java/com/alo/sqllibscomparison/infrastructure/jooq/generated/tables/records/TaskRecord.java) to [`Task`](/src/main/kotlin/com/alo/sqllibscomparison/domain/TodoList.kt#L9):
```kotlin
 private fun TaskRecord.toTask() = Task(
        name = this.name,
        status = when (this.status) {
            TODO -> Status.TODO
            DONE -> Status.DONE
        }
    )
```
[Source](/src/main/kotlin/com/alo/sqllibscomparison/infrastructure/persistence/jooq/TodoListDomainMapper.kt).

Notice that we are not dealing with abstracted results, we have specific record definitions both for TodoListRecord
 and TaskRecord.

In terms of type-safety, we are totally covered, since Jooq generate specific record for each table with all the
 types defined in out schema, **we are safe at compile time :)**

### Querying
If you look at different websites, blogs, docs, all samples are using simple queries on simple tables, but in a real
 world, apps usually are more complex, it means that we would need to deal with more complex queries almost all the time, 
 here I would like to compare how these different libs tackle these problems.
  
#### One to Many relationships
Querying and mapping back results from one table is simple, even many-to-one relationships are easy to map because
 maps are row to dto/class; but one-to-many relationships are always tricky to do in almost all sql libs because
 it's the typical case where two worlds, sql and oop, differ.

Let's see an example in action to understand the problem, given this query on our schema:
```sql
select * from todo_list left join task on task.todo_list_id = todo_list.id order by todo_list.created desc
```
Then, this is the resultset that we get in return (we have omitted some columns of the real result)
```diff
 +--------+-------------+--------+-------------+--------------+--------+------+ 
 |id      |name         |id      |todo_list_id |name          |position|status| 
 +--------+-------------+--------+-------------+--------------+--------+------+ 
-|4db3b6ae|Weekend      |78ba8aed|4db3b6ae-3257|Get drunk     |       0|DONE  | 
+|202dba03|Pizza friday |f6182487|202dba03-0505|Pineapple     |       0|TODO  | 
+|202dba03|Pizza friday |f7ea6136|202dba03-0505|Mozzarella    |       2|TODO  | 
+|202dba03|Pizza friday |2d1fd77b|202dba03-0505|Tomato        |       1|TODO  | 
!|6ff7334f|Superman list|ce6b5b23|6ff7334f-b270|Save the world|       0|TODO  | 
 +--------+-------------+--------+-------------+--------------+--------+------+ 
``` 
As you can see, mapping the relationship of Todolist with multiple Tasks is not easy, tt means that we will have to
 group rows before map them or use special mappers/result-handlers to do so.
 
##### Exposed
Since `Exposed` is returning back always abstractions of rows, we will have to group them manually before mapping
 them back to our domain.
 
This is the one to many query:
```kotlin
override fun listAll(): List<TodoList> = transaction {
        TodoLists
            .leftJoin(Tasks)
            .selectAll()
            .orderBy(TodoLists.created, SortOrder.DESC)
            .toList()
            .let(::toMultipleTodoLists)
    }
```
[Source](/src/main/kotlin/com/alo/sqllibscomparison/infrastructure/persistence/exposed/TodoListRepository.kt#20)  

An this is how we group and map the rows to the domain:
```kotlin
    private val groupByTodoListId: (ResultRow) -> UUID = { row -> row[TodoLists.id] }

    fun toMultipleTodoLists(rows: List<ResultRow>): List<TodoList> =
        rows.groupBy(groupByTodoListId) // group all rows by the todo-list they belong
            .values
            .map { groupedRows -> toSingleTodoList(groupedRows) } // map a group of rows to a single todo-list
            .toList()

    fun toSingleTodoList(rows: List<ResultRow>): TodoList {
        val sorted = rows.sortedBy { it[Tasks.position] } // sort tasks
        val firstRow = sorted.first() // since we have a list of rows with th same todo-list info, we only need one
        val tasks = firstRow.getOrNull(Tasks.id)?.let { sorted.map { row -> row.toTask() } } ?: emptyList() // convert each row to a task
        return firstRow.toTodoList().copy(tasks = tasks)
    }

    private fun ResultRow.toTodoList() = ... // check previous exposed section
    private fun ResultRow.toTask() = ... // check previous exposed section
```
[Source](/src/main/kotlin/com/alo/sqllibscomparison/infrastructure/persistence/exposed/TodoListDomainMapper.kt)  


With exposed we have to manually group rows by todo-list before map a single row to the domain, this lib is not
 providing any special method or third party integration to do so.

Even is manual work, since Exposed is simple and easy to use, if you are familiar with SQL it shouldn't be a big deal.

##### Jooq
As we explained in the previous section, JOOQ provide multiple ways to fetch data, and in our example we are using 
 [strongly typed records](https://www.jooq.org/doc/3.12/manual/sql-execution/fetching/record-vs-tablerecord/).
Having this in mind, JOOQ provide plenty of API methods to handle the grouping automatically for typed DB Records.

One of them is [`fetchGroups`](https://www.jooq.org/javadoc/latest/org.jooq/org/jooq/ResultQuery.html#fetchGroups(org.jooq.Table)), this method allow us to group by table.

```kotlin
override fun listAll(): List<TodoList> =
        jooq.select()
            .from(TODO_LIST)
            .leftJoin(TASK)
            .on(TASK.TODO_LIST_ID.eq(TODO_LIST.ID))
            .orderBy(TODO_LIST.CREATED.desc())
            .fetchGroups(TODO_LIST) // The result type is -> Map<TodoListRecord, Result<Record>>
            .mapValues { it.value.into(TASK).toList() } // The result type is -> Map<TodoListRecord, List<TaskRecord>>
            .map { (todoListRecord, taskRecords) -> TodoListDomainMapper.toTodoList(todoListRecord, taskRecords) }
```
[Source](/src/main/kotlin/com/alo/sqllibscomparison/infrastructure/persistence/jooq/TodoListRepository.kt#18)  

Now we only need to convert a tuple of [TodolistRecord](/src/main/java/com/alo/sqllibscomparison/infrastructure/jooq/generated/tables/records/TodoListRecord.java) and its corresponding [TaskRecord](/src/main/java/com/alo/sqllibscomparison/infrastructure/jooq/generated/tables/records/TaskRecord.java) to [TodoList](/src/main/kotlin/com/alo/sqllibscomparison/domain/TodoList.kt#) domain.

```kotlin
 fun toTodoList(
        todoListRecord: TodoListRecord,
        taskRecords: List<TaskRecord>
    ): TodoList = todoListRecord.toTodoList().copy(tasks = taskRecords.sortedBy { it.position }.map { it.toTask() })

    private fun TodoListRecord.toTodoList() = ...// check previous jooq section

    private fun TaskRecord.toTask() = ...// check previous jooq section
```
[Source](/src/main/kotlin/com/alo/sqllibscomparison/infrastructure/persistence/jooq/TodoListDomainMapper.kt)  

Given the typesafe nature and the extense API of JOOQ, it is easy to tackle the one-to-many problem in many ways, the
 one that we saw is just one of them. 
 
If we wanted to automate this we could do it with a [record mapper providers](https://www.jooq.org/doc/latest/manual/sql-execution/fetching/pojos-with-recordmapper-provider/).
 
#### Sub-querying
A [subquery](https://www.w3resource.com/sql/subqueries/understanding-sql-subqueries.php) is a SQL query nested inside
 a larger query, and that's another complex thing to tackle with any ORM, you will have to know the framework and use
  special functions or plain sql to achieve this.
  
Coming back to our case, one of the problems that we have to solve is to list all the list not-completed, the ones
 with task still pending to-do.
 
Maybe it seems simple, but with one-to-many relationship in the middle it gets complicated, but hopefully sub-querying
 comes to the rescue:
- Create a simple join query that gives us all the todoListId's that contain a task in `TODO` status
- Query all todoList with all their tasks (`TODO` and `DONE`) with the Ids from the previous query
- Mix all together with a subquery.

Ta-da! :
```sql
select * from todo_list
left join task on task.todo_list_id = todo_list.id
where task.todo_list_id in (
    select distinct on (todo_list.id) todo_list.id 
    from todo_list left  join task on task.todo_list_id = todo_list.id 
    where task.status = 'TODO'
) 
order by "public"."todo_list"."created" desc
``` 
 
##### Exposed
It is pretty straight forward and close to the real sql to do subquerying with Exposed: 
```kotlin
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
```
##### Jooq
With jooq, even it is easy to do it, it becomes a bit verbose:
 ```kotlin
val notCompleted = jooq
            .select(TODO_LIST.ID)
            .distinctOn(TODO_LIST.ID)
            .from(TODO_LIST)
            .leftJoin(TASK)
            .on(TASK.TODO_LIST_ID.eq(TODO_LIST.ID))
            .where(TASK.STATUS.eq(TaskStatus.TODO))
jooq.select()
    .from(TODO_LIST)
    .leftJoin(TASK)
    .on(TASK.TODO_LIST_ID.eq(TODO_LIST.ID))
    .where(TASK.TODO_LIST_ID.`in`(notCompleted))
    .orderBy(TODO_LIST.CREATED.desc())
    .fetchGroups(TODO_LIST)
    .mapValues { it.value.into(TASK).toList() }
    .map { (todoListRecord, taskRecords) -> TodoListDomainMapper.toTodoList(todoListRecord, taskRecords) }
 ```

#### Dynamic querying

##### Exposed

##### Jooq
 https://blog.jooq.org/tag/query-dsl/
