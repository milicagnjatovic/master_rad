package org.example;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import org.json.JSONArray;
import org.json.JSONObject;

//import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;

@Path("/grader")
public class SolutionController {
    @POST
    @Path("/generate")
    @Consumes(MediaType.APPLICATION_JSON)
    public String generateSolution(String body){
        System.out.println("[generateSolution]");
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
        System.out.println("[getSolution] " + id);
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
        System.out.println("[checkSolution]");
//        System.out.println(body);
        try {
            return TaskHandler.checkTask(body).toString();
        } catch (IOException | InterruptedException e) {
            JSONObject err = new JSONObject();
            err.put("ok", false);
            err.put("message", e.getMessage());
            return err.toString();
        }
    }

    @POST
    @Path("/createDatabase")
    public String createDatabase(){
        try {
            ProcessBuilder generateSolutionsPB = new ProcessBuilder("./scripts/stud2020/create.sh");
            Process generateSolutionP = generateSolutionsPB.start();
            TaskHandler.printConsoleResponse(generateSolutionP.getInputStream());
            generateSolutionP.waitFor();
            generateSolutionP.destroy();
            return "Databsse created";
        } catch (IOException | InterruptedException e) {
            return "Error: " + e.getMessage();
        }
    }

}
