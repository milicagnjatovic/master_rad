package org.example.Tables;

import javax.persistence.*;

@Entity
@Table(name = "MESSAGES")
public class Messages {
    @EmbeddedId
    public MessageID Id;

    @Column(name = "QUESTION")
    public String Question;

    @Column(name = "PROFESSOR_ID", nullable = false)
    public Integer ProfessorId;

    @Column(name = "RESPONSE")
    public String Response;

    @OneToOne
    @JoinColumns({
            @JoinColumn(name = "TASK_ID", referencedColumnName = "TASK_ID", insertable = false, updatable = false),
            @JoinColumn(name = "STUDENT_ID", referencedColumnName = "USER_ID", insertable = false, updatable = false)
    })
    public Submission SubmissionInQuestion;

//    CREATE TABLE MESSAGES (
//            STUDENT_ID INTEGER NOT NULL,
//            PROFESSOR_ID INTEGER NOT NULL,
//            TASK_ID INTEGER NOT NULL,
//            MESSAGE VARCHAR(256),
//            RESPONSE VARCHAR(256),
//    PRIMARY KEY (STUDENT_ID, PROFESSOR_ID, TASK_ID),
//    FOREIGN KEY FK_SUBMISSION (STUDENT_ID, TASK_ID) REFERENCES SUBMISSIONS(USER_ID, TASK_ID),
//    FOREIGN KEY FK_PROFESSOR (PROFESSOR_ID) REFERENCES USERS(ID)
//            );

}
