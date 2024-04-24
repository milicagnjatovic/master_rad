--  CREATE DATABASE GRADER;

CONNECT TO GRADER;

 DROP TABLE IF EXISTS ROLES;
 CREATE TABLE ROLES (
     ID SMALLINT NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
     NAME VARCHAR(10),
     DESCRIPTION VARCHAR(255),
     CREATED_DATE DATETIME DEFAULT CURRENT_TIMESTAMP
 );

 INSERT INTO ROLES(NAME, DESCRIPTION)
 VALUES
     ('admin', 'Namenjeno administratorima.'),
     ('professor', 'Namenjeno profesorima.'),
     ('student', 'Namenjeno studentima MATFa.'),
     ('others', 'Namenjeno ostalim korisnicima.');

 DROP TABLE IF EXISTS USERS;
 CREATE TABLE USERS (
     ID INTEGER NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
     USERNAME VARCHAR(10) UNIQUE NOT NULL,
     PASSWORD VARCHAR(50) NOT NULL,
     EMAIL VARCHAR(30) UNIQUE NOT NULL,
     FIRST_NAME VARCHAR(20),
     LAST_NAME VARCHAR(20),
     CREATED_DATE DATETIME DEFAULT CURRENT_TIMESTAMP,
     ROLE_ID SMALLINT NOT NULL,
     ACTIVE BOOLEAN,
     FOREIGN KEY FK_ROLE (ROLE_ID) REFERENCES ROLES(ID)
 );

 DROP TABLE IF EXISTS GRADERS;
 CREATE TABLE GRADERS (
     ID INTEGER NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
     NAME VARCHAR(10) NOT NULL UNIQUE,
     ENDPOINT VARCHAR(50) NOT NULL UNIQUE,
     ACTIVE BOOLEAN,
     CREATED_DATE DATETIME DEFAULT CURRENT_TIMESTAMP
 );

 DROP TABLE IF EXISTS TASKS;
 CREATE TABLE TASKS (
     ID INTEGER NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
     GRADER_ID INTEGER NOT NULL,
     FOREIGN KEY FK_GRADER (GRADER_ID) REFERENCES GRADERS(ID),
     TEXT VARCHAR(1000) NOT NULL,
     SOLUTION VARCHAR(2500) NOT NULL,
     ORDERING VARCHAR(20),
     LAST_GENERATED_DATE DATETIME DEFAULT CURRENT_TIMESTAMP
 );


 DROP TABLE IF EXISTS SUBMISSIONS;
 CREATE TABLE SUBMISSIONS (
     TASK_ID INTEGER NOT NULL,
     USER_ID INTEGER NOT NULL,
     IS_CORRECT BOOLEAN,
     QUERY VARCHAR(2500),
     MESSAGE VARCHAR(50),
     NO_SUBMISSIONS INTEGER,
     NO_CORRECT_SUBMISSIONS INTEGER,
     LAST_UPDATE_TIME DATETIME DEFAULT CURRENT_TIMESTAMP,
     WAITING_FOR_RESPONSE BOOLEAN DEFAULT FALSE,
     PRIMARY KEY (TASK_ID, USER_ID),
     FOREIGN KEY FK_TASK (TASK_ID) REFERENCES TASKS(ID),
     FOREIGN KEY FK_USER (USER_ID) REFERENCES USERS(ID)
 );

DROP TABLE IF EXISTS ROLE_GRADER_PERMISSION;
 CREATE TABLE ROLE_GRADER_PERMISSION (
     ROLE_ID INTEGER NOT NULL,
     GRADER_ID INTEGER NOT NULL,
     PRIMARY KEY (ROLE_ID, GRADER_ID),
     FOREIGN KEY FK_ROLE (ROLE_ID) REFERENCES ROLES(ID),
     FOREIGN KEY FK_GRADER (GRADER_ID) REFERENCES GRADERS(ID)
 );

 DROP TABLE IF EXISTS MESSAGES;
 CREATE TABLE MESSAGES (
     STUDENT_ID INTEGER NOT NULL,
     PROFESSOR_ID INTEGER NOT NULL,
     TASK_ID INTEGER NOT NULL,
     MESSAGE VARCHAR(256),
     RESPONSE VARCHAR(256),
     PRIMARY KEY (STUDENT_ID, PROFESSOR_ID, TASK_ID),
     FOREIGN KEY FK_SUBMISSION (STUDENT_ID, TASK_ID) REFERENCES SUBMISSIONS(USER_ID, TASK_ID),
     FOREIGN KEY FK_PROFESSOR (PROFESSOR_ID) REFERENCES USERS(ID)
 );
