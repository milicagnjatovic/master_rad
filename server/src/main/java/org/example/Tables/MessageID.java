package org.example.Tables;

import org.json.JSONObject;

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

    public MessageID(JSONObject obj){
        System.out.println(obj);
        Integer userId = obj.optInt("userId", -1);
        Integer taskId = obj.optInt("taskId", -1);
        if (userId==-1 || taskId==-1)
            return;
        this.StudentId = userId;
        this.TaskId = taskId;
    }

    public MessageID(){}
}
