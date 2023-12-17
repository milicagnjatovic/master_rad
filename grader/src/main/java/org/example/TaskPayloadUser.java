package org.example;

import org.json.JSONObject;

public class TaskPayloadUser extends TaskPayload {
    public String requestId;
    public TaskPayloadUser(JSONObject obj) {
        super(obj);
        this.requestId = obj.getString("requestId"); // + new Date().getTime();
    }

    @Override
    public String toString() {
        return this.requestId + ' ' + this.taskId.toString() +
            '\n' + this.solution;
    }
}
