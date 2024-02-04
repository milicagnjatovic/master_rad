package org.example.Conreollers;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import org.example.RequestHandlers;
import org.example.Tables.Grader;
import org.example.Tables.Task;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.ws.rs.core.MediaType;
import java.io.IOException;



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
//        System.out.println(body);

//        try {
//            String response = RequestHandlers.sendRequest("checkSolution", body);
//            return response;
//        } catch (IOException e) {
//            return "ERROR | " + e.getMessage();
//        }
        return "ok";
    }

    @POST
    @Path("/checkTaskBulk")
    @Consumes(MediaType.APPLICATION_JSON)
    public String checkTaskBulk(String body){
        System.out.println("[check task bulk]");
        System.out.println(body);
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
