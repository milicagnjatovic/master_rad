package org.example;

import org.json.JSONObject;

public class TaskPayload {
    public static Integer IDS = 0;
    public String solution;
    public String sorting;
    public Integer taskId;

    public TaskPayload(JSONObject obj){
        this.sorting = obj.optString("ordering", "");
        this.solution = obj.getString("solution")
                .toUpperCase()
                .replaceAll("CURRENT[_ ]DATE", "DATE('10.10.2023')")
                .replaceAll("CURRENT[_ ]TIME", "TIME('10:55')");
        if(!this.sorting.isBlank()){
            this.solution = solution.replace(";",  " ORDER BY " + sorting + ";");
        }
        this.taskId = obj.optInt("taskId", IDS++);
    }

    public String getSolution() {
        return solution;
    }
}
