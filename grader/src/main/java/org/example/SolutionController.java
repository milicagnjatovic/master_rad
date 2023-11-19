package org.example;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.awt.*;
import java.io.IOException;

@Path("/generateSolution")
public class SolutionController {
    @POST
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

}
