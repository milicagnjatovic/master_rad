package org.example;

import org.json.JSONObject;

import java.util.Date;

public class TaskPayloadUser extends TaskPayload {
    public String userId;
    public TaskPayloadUser(JSONObject obj) {
        super(obj);
        this.userId = obj.getString("userId") + new Date().getTime();
    }

    @Override
    public String toString() {
        return this.userId + ' ' + this.taskId.toString() +
            '\n' + this.solution;
    }
}
