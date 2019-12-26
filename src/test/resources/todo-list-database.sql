-- Schema
CREATE DATABASE TODO_LISTS;

CREATE TABLE TODO_LIST (
    id                      UUID                   NOT NULL PRIMARY KEY,
    name                    TEXT                   NOT NULL ,
    created                 TIMESTAMPTZ            NOT NULL DEFAULT now(),
    updated                 TIMESTAMPTZ            NOT NULL DEFAULT now()
);

CREATE TYPE TASK_STATUS AS ENUM (
    'TODO',
    'DONE'
);

CREATE TABLE TASK (
    id                      UUID                   NOT NULL PRIMARY KEY,
    todo_list_id            UUID                   REFERENCES TODO_LIST(id) ON DELETE CASCADE,
    name                    TEXT                   NOT NULL,
    position                smallint               NOT NULL,
    status                  TASK_STATUS            NOT NULL DEFAULT 'TODO',
    created                 TIMESTAMPTZ            NOT NULL DEFAULT now(),
    updated                 TIMESTAMPTZ            NOT NULL DEFAULT now()
);

ALTER TABLE TASK ADD UNIQUE (id, name);

-- Data

INSERT INTO TODO_LIST (id, name, created)
    VALUES ('d3787c68-0d5b-474e-9a25-f5a0d079fa4b', 'Today', '01/11/2018 02:00:00'::TIMESTAMP);

INSERT INTO TASK (id, name, todo_list_id, status, position)
    VALUES ('0236c81d-3620-41cd-9a6b-322f78f5bfbf', 'Get up','d3787c68-0d5b-474e-9a25-f5a0d079fa4b', 'DONE', 0);
INSERT INTO TASK (id, name, todo_list_id, status, position)
    VALUES ('41b3407d-9e81-48ef-bf4c-b78f24a026e7', 'Survive', 'd3787c68-0d5b-474e-9a25-f5a0d079fa4b', 'TODO', 1);
INSERT INTO TASK (id, name, todo_list_id, status, position)
    VALUES ('f9f37365-565a-4b11-bc52-ea214c66a024', 'Go to bed', 'd3787c68-0d5b-474e-9a25-f5a0d079fa4b', 'TODO', 2);


INSERT INTO TODO_LIST (id, name, created)
    VALUES ('fed97520-f3cb-4ff0-aec7-ce6596768579', 'Shopping list', '02/11/2018 02:00:00'::TIMESTAMP);

INSERT INTO TASK (id, name, todo_list_id, status, position)
    VALUES ('8be1086d-2d85-402f-b0de-31c74c9ba1e3', 'Human food','fed97520-f3cb-4ff0-aec7-ce6596768579', 'DONE', 0);
INSERT INTO TASK (id, name, todo_list_id, status, position)
    VALUES ('6a786724-d972-4ec4-812f-096a63f82a31', 'Cat food', 'fed97520-f3cb-4ff0-aec7-ce6596768579', 'DONE', 1);

INSERT INTO TODO_LIST (id, name, created) VALUES ('6ff7334f-b270-460b-a082-8854e567c38c', 'Superman list', '03/11/2018 02:00:00'::TIMESTAMP);

INSERT INTO TASK (id, name, todo_list_id, status, position)
    VALUES ('ce6b5b23-97e3-4e68-a201-434089d1da7b', 'Save the world','6ff7334f-b270-460b-a082-8854e567c38c', 'TODO', 0);

INSERT INTO TODO_LIST (id, name, created)
VALUES ('202dba03-0505-4192-a374-503cab38a94b', 'Pizza friday', '04/11/2018 02:00:00'::TIMESTAMP);

INSERT INTO TASK (id, name, todo_list_id, status, position)
    VALUES ('f6182487-0a09-4a4a-8d5c-00a69460002c', 'Pineapple','202dba03-0505-4192-a374-503cab38a94b', 'TODO', 0);
INSERT INTO TASK (id, name, todo_list_id, status, position)
    VALUES ('2d1fd77b-7435-400d-b7f7-3914072ab958', 'Tomato','202dba03-0505-4192-a374-503cab38a94b', 'TODO', 1);
INSERT INTO TASK (id, name, todo_list_id, status, position)
    VALUES ('f7ea6136-c208-4f5c-adf5-81f305fa47d9', 'Mozzarella','202dba03-0505-4192-a374-503cab38a94b', 'TODO', 2);

INSERT INTO TODO_LIST (id, name, created)
    VALUES ('4db3b6ae-3257-46b6-b3c3-d8b91239632a', 'Weekend', '05/11/2018 02:00:00'::TIMESTAMP);

INSERT INTO TASK (id, name, todo_list_id, status, position)
    VALUES ('78ba8aed-c6fd-4233-8459-21333df4c170', 'Get drunk','4db3b6ae-3257-46b6-b3c3-d8b91239632a', 'DONE', 0);
