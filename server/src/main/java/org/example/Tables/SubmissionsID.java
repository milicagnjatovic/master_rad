package org.example.Tables;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;

public class SubmissionsID implements Serializable {
    @Id
    @Column(name = "USER_ID")
    public Integer UserId;

    @Column(name = "TASK_ID")
    public Integer TaskId;

    public SubmissionsID(Integer userId, Integer taskId){
        this.TaskId = taskId;
        this.UserId = userId;
    }
}
