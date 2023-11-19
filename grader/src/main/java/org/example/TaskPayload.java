package org.example;

import org.json.JSONObject;

public class TaskPayload {
    public static Integer IDS = 0;
    public String task;
    public String solution;
    public String sorting;
    public Integer id;

    public TaskPayload(JSONObject obj){
        this.task = obj.getString("task");
        this.solution = obj.getString("solution");
        this.sorting = obj.optString("ordering", "");
        this.id = IDS;
        IDS++;
    }

    public String getSolution() {
        if(sorting.isBlank()){
            return solution;
        }
        return solution.replace(";", "") + " ORDER BY " + sorting + ";";
    }
}
