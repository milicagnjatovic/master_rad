package org.example.Tables;

import javax.persistence.*;
import java.io.Serializable;

@Embeddable
public class MessageID implements Serializable {
    @Column(name = "STUDENT_ID", nullable = false)
    public Integer StudentId;

    @Column(name = "TASK_ID", nullable = false)
    public Integer TaskId;
}
