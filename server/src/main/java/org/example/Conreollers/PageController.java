package org.example.Conreollers;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import org.example.FileUtil;
import org.example.Tables.Submission;
import org.example.Tables.Task;
import org.example.Views.TaskStatistic;
import org.example.Views.UserStatistic;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/page")
public class PageController {

    @GET
    @Path("/getAllTasks")
    @Produces(MediaType.APPLICATION_JSON)
    public String getAllTasks(){
        System.out.println("[getAllTasks]");
        return FileUtil.readFromFile(FileUtil.FILE_WITH_TASKS);
    }

    @GET
    @Path("/refreshTasks")
    public String refreshTasks(){
        System.out.println("[refreshAllTasks]");

        List<Task> tasks = Task.getAllTasks();
        for (Task task: tasks)
            System.out.println(task);

        JSONArray tasksJSON = Task.tasksToJSONArray(tasks);

        FileUtil.writeToFile(FileUtil.FILE_WITH_TASKS, tasksJSON.toString());

        return tasksJSON.toString();
    }


    @POST
    @Path("/getTasksForUser")
    public String getTasksForUser(String body){
        System.out.println("[getTasksForUser]");

        JSONObject request = new JSONObject(body);
        Integer userId = request.optInt("userId", -1);

        List<Submission> submissions = Submission.getSubmissionsForUser(userId);

        JSONArray ret = new JSONArray();

        for (Submission submission: submissions) {
            JSONObject obj = new JSONObject();
            obj.put("taskId", submission.Task.Id);
            obj.put("noCorrect", submission.CorrectSubmissions);
            obj.put("noTotalSubmissions", submission.TotalSubmissions);
            obj.put("isWaitingForResponse", submission.WaitingForResponse);
            ret.put(obj);
        }

        return ret.toString();
    }

    @GET
    @Path("/getStats")
    public String getTaskStatistics(){
        System.out.println("[getStats]");
        return FileUtil.readFromFile(FileUtil.FILE_WITH_STATS);
    }

    @GET
    @Path("/refreshStats")
    public String refreshStats(){
        System.out.println("[refreshStats]");
        List<TaskStatistic> taskStats = TaskStatistic.getAllStats();
        List<UserStatistic> userStats = UserStatistic.getAllStats();
        JSONArray taskJSON = TaskStatistic.getJSONfromList(taskStats);
        JSONArray userJSON = UserStatistic.getJSONfromList(userStats);

        JSONObject obj = new JSONObject();
        obj.put("taskStats", taskJSON);
        obj.put("userStats", userJSON);

        FileUtil.writeToFile(FileUtil.FILE_WITH_STATS, obj.toString());

        return obj.toString();
    }
}
