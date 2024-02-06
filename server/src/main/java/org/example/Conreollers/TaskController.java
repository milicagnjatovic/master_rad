package org.example.Conreollers;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import org.example.RequestHandlers;
import org.example.Tables.Grader;
import org.example.Tables.Submission;
import org.example.Tables.SubmissionID;
import org.example.Tables.Task;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.persistence.PersistenceException;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.Date;


@Path("/server")
public class TaskController {


    @POST
    @Path("/addTasks")
    @Consumes(MediaType.APPLICATION_JSON)
    public String addTasks(String body){
        System.out.println("[addTasks]");
        System.out.println(body);

        JSONObject request = new JSONObject(body);

        if(!request.has("graderId") || !request.has("tasks")){
            JSONObject ret = new JSONObject();
            ret.put("message", "Error | Request must have following format: {graderId: number, tasks: [...]}");
            return ret.toString();
        }

        Integer graderId = request.getInt("graderId");
        Grader grader = Grader.getById(graderId);
        if (grader == null){
            return "Error | Grader does not exists";
        }

        JSONArray taskArray = request.getJSONArray("tasks");

        JSONObject insertionResponse = Task.insertTasks(taskArray, grader);
        JSONArray errors = insertionResponse.getJSONArray("errors");
        JSONArray tasks = insertionResponse.getJSONArray("tasks");

        JSONObject graderRequest = new JSONObject();
        graderRequest.put("tasks", tasks);
        String graderResponse;
        try {
            graderResponse = RequestHandlers.sendRequest(grader.Endpoint, RequestHandlers.GraderAction.GENERATE, graderRequest.toString());
        } catch (IOException e) {
            graderResponse = "Error | " + e.getMessage();
        }

        JSONObject returnObject = new JSONObject();
        returnObject.put("errors", errors);
        returnObject.put("graderResponse", graderResponse);

        return returnObject.toString();
    }

    @POST
    @Path("/checkTask")
    @Consumes(MediaType.APPLICATION_JSON)
    public String checkTask(String body){
        System.out.println("[check task]");
        System.out.println(body);

        try {
            JSONObject request = new JSONObject(body);
            Integer userId = request.getInt("userid");
            Integer taskId = request.getInt("taskid");
            String solution = request.getString("solution");
            SubmissionID submissionID = new SubmissionID(userId, taskId);

            Submission submission = Submission.getById(submissionID);
            System.out.println(submission);
            if (submission != null && submission.WaitingForResponse){
                return "Already waiting for response";
            }

            if (submission == null) {
                submission = new Submission();
                submission.SubmissionId = submissionID;
                Task task = Task.getById(submissionID.TaskId);
                if (task==null)
                    return "Error | Task does not exist";
                submission.Task = task;
                System.out.println("Submission null " + submission.Task.Text);
                System.out.println("ordering null " + submission.Task.Ordering);
            }

            submission.WaitingForResponse = true;
            submission.TotalSubmissions = submission.TotalSubmissions + 1;
            submission.Query = solution;
            submission.Message = null;
            System.out.println("Task " + submission.Task);

            JSONObject graderRequest = new JSONObject();
            graderRequest.put("requestId", taskId + "-" + userId);
            graderRequest.put("taskId", taskId);
            graderRequest.put("solution", submission.Query);
            if (!submission.Task.Ordering.isEmpty())
                graderRequest.put("ordering", submission.Task.Ordering);

            Grader grader = Grader.activeGraders.get(submission.Task.Grader.Id);
            String requestEndpoint = grader.Endpoint;

            System.out.println(graderRequest.toString());
            System.out.println(requestEndpoint);
            System.out.println("--------------------");

            System.out.println(submission.User);
            System.out.println(submission.Query);
            System.out.println(submission.Query.length());
            Submission.updateOrInsert(submission);

            String response = RequestHandlers.sendRequest(requestEndpoint, RequestHandlers.GraderAction.CHECK, graderRequest.toString());

            JSONObject responseJson = new JSONObject(response);
            submission.Message = responseJson.getString("message");
            submission.IsCorrect = responseJson.getBoolean("ok");
            if(submission.IsCorrect)
                submission.CorrectSubmissions = submission.CorrectSubmissions+1;
            submission.WaitingForResponse = false;
            Submission.updateOrInsert(submission);

            return response;
        } catch (Exception  e) {
            return "ERROR | " + e.getMessage();
        }
//        return "ok";
    }

    @POST
    @Path("/checkTaskBulk")
    @Consumes(MediaType.APPLICATION_JSON)
    public String checkTaskBulk(String body){
        System.out.println("[check task bulk]");
        System.out.println(body);

//        send unsent tasks message null && waiting for response null
//        try {
//            String response = RequestHandlers.sendRequest("checkSolutionBulk", body);
//            return response;
//        } catch (IOException e) {
//            return "ERROR | " + e.getMessage();
//        }
        return "ok";
    }

    @POST
    @Path("/updateTasks")
    @Consumes(MediaType.APPLICATION_JSON)
    public String updateTask(String body){
        System.out.println("[check task bulk]");
//        System.out.println(body);
//        TODO update task in database
//        try {
//            String response = RequestHandlers.sendRequest("generate", body);
//            return response;
//        } catch (IOException e) {
//            return "ERROR | " + e.getMessage();
//        }
        return "ok";
    }
}
