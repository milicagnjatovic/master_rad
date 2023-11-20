package org.example;

import org.json.JSONObject;

public class TaskPayload {
    public static Integer IDS = 0;
    public String solution;
    public String sorting;
    public Integer id;

    public TaskPayload(JSONObject obj){
        this.solution = obj.getString("solution")
                .toUpperCase()
                .replaceAll("CURRENT[_ ]DATE", "DATE('10.10.2023')")
                .replaceAll("CURRENT[_ ]TIME", "TIME('10:55')");
        this.sorting = obj.optString("ordering", "");
        this.id = obj.optInt("id", IDS++);
    }

    public String getSolution() {
        if(sorting.isBlank()){
            return solution;
        }
        return solution.replace(";", "") + " ORDER BY " + sorting + ";";
    }
}
