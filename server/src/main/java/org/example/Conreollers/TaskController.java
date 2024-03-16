package org.example.Conreollers;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import org.example.RequestHandlers;
import org.example.Tables.Grader;
import org.example.Tables.Submission;
import org.example.Tables.SubmissionID;
import org.example.Tables.Task;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;


@Path("/server")
public class TaskController {


    @POST
    @Path("/addTasks")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
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
    @Produces(MediaType.APPLICATION_JSON)
    public String checkTask(String body){
        System.out.println("[check task]");
//        System.out.println(body);

        try {
            JSONObject request = new JSONObject(body);
            Integer userId = request.getInt("userId");
            Integer taskId = request.getInt("taskId");
            String solution = request.getString("solution");
            SubmissionID submissionID = new SubmissionID(userId, taskId);

            Submission submission = Submission.getById(submissionID);
            if (submission != null && submission.WaitingForResponse){
                return "Already waiting for response " + userId + " " + taskId;
            }

            if (submission == null) {
                submission = new Submission();
                submission.SubmissionId = submissionID;
                Task task = Task.getById(submissionID.TaskId);
                if (task==null)
                    return "Error | Task does not exist " + submissionID.TaskId;
                submission.Task = task;
            }

            submission.WaitingForResponse = true;
            submission.TotalSubmissions = submission.TotalSubmissions + 1;
            submission.Query = solution;
            submission.Message = null;
            Submission.updateOrInsert(submission);

            JSONObject graderRequest = Submission.prepareGraderRequest(userId, taskId, submission.Query, submission.Task.Ordering);

            Grader grader = Grader.activeGraders.get(submission.Task.Grader.Id);
            String requestEndpoint = grader.Endpoint;

            String response = RequestHandlers.sendRequest(requestEndpoint, RequestHandlers.GraderAction.CHECK, graderRequest.toString());

            submission.graderResponsePayloadToSubmission(response);
            Submission.updateOrInsert(submission);

            return response;
        } catch (Exception  e) {
            return "ERROR | " + e.getMessage();
        }
    }

    @POST
    @Path("/checkTaskBulk")
    @Consumes(MediaType.APPLICATION_JSON)
    public String checkTaskBulk(String body){
        System.out.println("[check task bulk]");
        System.out.println(body);
        List<Submission> submissions = Submission.getSubmissionsWaiting();
        System.out.println("[Submissions for checking]" + submissions);
//        GraderID : BulkRequestPayload
        Map<Integer, JSONArray> graderRequests = Submission.prepareGraderBulkRequest(submissions);
        System.out.println(graderRequests);

        JSONArray responses = new JSONArray();
        try {
            for (Integer graderId : graderRequests.keySet()){
                 Grader grader = Grader.activeGraders.get(graderId);
                 JSONArray graderRequestPayload = graderRequests.get(graderId);
                 try {
                     System.out.println("Grader request " + graderId + " " + graderRequestPayload);
//                     sending bulk request for grader wirh graderId
                     String graderResponse = RequestHandlers.sendRequest(grader.Endpoint, RequestHandlers.GraderAction.CHECKBULK, graderRequestPayload.toString());
                     responses.put(graderResponse);

                     JSONArray responsesForBulk = new JSONObject(graderResponse).getJSONArray("results");

//                     going thorough responses and preparing for update
                     List<Submission> submissionsForUpdate = new ArrayList<>();
                     for (Integer i=0; i<responsesForBulk.length(); i++) {
                         JSONObject obj = responsesForBulk.getJSONObject(i);
                         String requestId = obj.getString("requestId");
                         Integer taskId = Integer.valueOf(requestId.split("-")[0]);
                         Integer userId = Integer.valueOf(requestId.split("-")[1]);
                         Boolean isCorrect = obj.getBoolean("ok");
                         String message = obj.getString("message");

                         submissionsForUpdate.add(new Submission(userId, taskId, isCorrect, message));
                     }
//                     update submissions
                     Submission.updateOrInsert(submissionsForUpdate);

                     System.out.println("GraderResponse " + graderId + " : " + graderResponse);

                 } catch (Exception e) {
                     System.out.println("Error | " + graderId + " " + e.getMessage());
                     responses.put(e.getMessage());
                 }
            }
//            JSONArray taskResponses = responses.getJSONArray("results");
            return responses.toString();
        } catch (Exception e) {
            return "ERROR | " + e.getMessage();
        }
    }

    @POST
    @Path("/updateTasks")
    @Consumes(MediaType.APPLICATION_JSON)
    public String updateTask(String body){
        System.out.println("[updateTask]");
        System.out.println(body);

        JSONObject request = new JSONObject(body);

        if(!request.has("graderId") || !request.has("tasks")){
            JSONObject ret = new JSONObject();
            ret.put("message", "Error | Request must have following format: {graderId, tasks: [{taskId, solution, ordering}, {}, ...]");
            return ret.toString();
        }

        Integer graderId = request.getInt("graderId");
        Grader grader = Grader.getById(graderId);
        if (grader == null){
            return "Error | Grader does not exists";
        }

        JSONArray taskArray = request.getJSONArray("tasks");

        JSONObject updateTasksResponse = Task.updateTasks(taskArray, grader);
        JSONArray errors = updateTasksResponse.getJSONArray("errors");
        JSONArray tasks = updateTasksResponse.getJSONArray("tasks");

        System.out.println("[Tasks] " + tasks);

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
}
