package org.example.Tables;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Id;
import java.io.Serializable;

@Embeddable
public class SubmissionID implements Serializable {
    @Column(name = "USER_ID", nullable = false)
    public Integer UserId;

    @Column(name = "TASK_ID", nullable = false)
    public Integer TaskId;

    public SubmissionID(Integer userId, Integer taskId){
        this.TaskId = taskId;
        this.UserId = userId;
    }

    public SubmissionID() {
    }

    @Override
    public String toString() {
        return this.UserId + " - " + this.TaskId;
    }

    @Override
    public boolean equals(Object obj) {
        return this.TaskId==((SubmissionID)obj).TaskId &&
                this.UserId == ((SubmissionID) obj).UserId;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
