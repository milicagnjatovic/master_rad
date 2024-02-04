package org.example.Tables;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;

@Embeddable
public class SubmissionsID implements Serializable {
    @Id
    @Column(name = "USER_ID", nullable = false)
    public Integer UserId;

    @Column(name = "TASK_ID", nullable = false)
    public Integer TaskId;

    public SubmissionsID(Integer userId, Integer taskId){
        this.TaskId = taskId;
        this.UserId = userId;
    }

    public SubmissionsID() {

    }
}
