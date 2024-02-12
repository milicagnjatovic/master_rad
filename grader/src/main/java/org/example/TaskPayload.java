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
                .replaceAll("(?i)CURRENT[_ ]DATE", "DATE('10.10.2023')")
                .replaceAll("(?i)CURRENT[_ ]TIME", "TIME('10:55')")
                .trim();
        if(!this.sorting.isBlank()){
            if (!this.solution.endsWith(";"))
                this.solution = this.solution + ';';
            // add sorting or append to the existing one
            String replacement = this.sorting.startsWith(",") ? sorting : " ORDER BY " + sorting;
            this.solution = solution.replace(";", replacement + ";");
        }
        this.taskId = obj.optInt("taskId", IDS++);
    }

    public String getSolution() {
        return solution;
    }
}
