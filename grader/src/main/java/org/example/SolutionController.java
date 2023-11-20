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

}
