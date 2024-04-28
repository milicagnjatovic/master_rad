package org.example.Tables;

import javax.persistence.*;
import java.io.Serializable;

@Embeddable
public class MessageID implements Serializable {
    @Column(name = "STUDENT_ID", nullable = false)
    public Integer StudentId;

    @Column(name = "TASK_ID", nullable = false)
    public Integer TaskId;

    public MessageID(Integer studentId, Integer taskId){
        this.StudentId = studentId;
        this.TaskId = taskId;
    }

    public MessageID(){}
}
