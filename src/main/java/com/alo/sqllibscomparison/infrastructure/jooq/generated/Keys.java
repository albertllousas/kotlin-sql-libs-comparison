/*
 * This file is generated by jOOQ.
 */
package com.alo.sqllibscomparison.infrastructure.jooq.generated;


import com.alo.sqllibscomparison.infrastructure.jooq.generated.tables.Task;
import com.alo.sqllibscomparison.infrastructure.jooq.generated.tables.TodoList;
import com.alo.sqllibscomparison.infrastructure.jooq.generated.tables.records.TaskRecord;
import com.alo.sqllibscomparison.infrastructure.jooq.generated.tables.records.TodoListRecord;

import javax.annotation.Generated;

import org.jooq.ForeignKey;
import org.jooq.UniqueKey;
import org.jooq.impl.Internal;


/**
 * A class modelling foreign key relationships and constraints of tables of 
 * the <code>public</code> schema.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.12.3"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Keys {

    // -------------------------------------------------------------------------
    // IDENTITY definitions
    // -------------------------------------------------------------------------


    // -------------------------------------------------------------------------
    // UNIQUE and PRIMARY KEY definitions
    // -------------------------------------------------------------------------

    public static final UniqueKey<TaskRecord> TASK_PKEY = UniqueKeys0.TASK_PKEY;
    public static final UniqueKey<TaskRecord> TASK_ID_NAME_KEY = UniqueKeys0.TASK_ID_NAME_KEY;
    public static final UniqueKey<TodoListRecord> TODO_LIST_PKEY = UniqueKeys0.TODO_LIST_PKEY;

    // -------------------------------------------------------------------------
    // FOREIGN KEY definitions
    // -------------------------------------------------------------------------

    public static final ForeignKey<TaskRecord, TodoListRecord> TASK__TASK_TODO_LIST_ID_FKEY = ForeignKeys0.TASK__TASK_TODO_LIST_ID_FKEY;

    // -------------------------------------------------------------------------
    // [#1459] distribute members to avoid static initialisers > 64kb
    // -------------------------------------------------------------------------

    private static class UniqueKeys0 {
        public static final UniqueKey<TaskRecord> TASK_PKEY = Internal.createUniqueKey(Task.TASK, "task_pkey", Task.TASK.ID);
        public static final UniqueKey<TaskRecord> TASK_ID_NAME_KEY = Internal.createUniqueKey(Task.TASK, "task_id_name_key", Task.TASK.ID, Task.TASK.NAME);
        public static final UniqueKey<TodoListRecord> TODO_LIST_PKEY = Internal.createUniqueKey(TodoList.TODO_LIST, "todo_list_pkey", TodoList.TODO_LIST.ID);
    }

    private static class ForeignKeys0 {
        public static final ForeignKey<TaskRecord, TodoListRecord> TASK__TASK_TODO_LIST_ID_FKEY = Internal.createForeignKey(com.alo.sqllibscomparison.infrastructure.jooq.generated.Keys.TODO_LIST_PKEY, Task.TASK, "task__task_todo_list_id_fkey", Task.TASK.TODO_LIST_ID);
    }
}
