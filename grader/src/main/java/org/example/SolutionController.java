package org.example;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Path("/grader")
public class SolutionController {
    @POST
    @Path("/generate")
    @Consumes(MediaType.APPLICATION_JSON)
    public String generateSolution(String body){
        System.out.println(body);
        try {
            JSONArray arr = new JSONObject(body).getJSONArray("tasks");
            TaskHandler.generateSolutions(arr);
        } catch (InterruptedException | IOException e) {
            return "Error occurred: " + e.getMessage();
        }
        return "done";
    }

    @GET
    @Path("/getSolution")
    public String getSolution(@QueryParam("id") String id){
        try {
            return Files.readString(Paths.get("results", id));
        } catch (IOException e) {
            return "Solution not found";
        }
    }

    @POST
    @Path("/checkSolution")
    @Consumes(MediaType.APPLICATION_JSON)
    public String checkSolution(String body){
//        JSONObject solution = new JSONObject(body);
        System.out.println(body);
        try {
            return TaskHandler.checkTask(body).toString();
        } catch (IOException | InterruptedException e) {
            JSONObject err = new JSONObject();
            err.put("ok", false);
            err.put("message", e.getMessage());
            return err.toString();
        }
    }

}
