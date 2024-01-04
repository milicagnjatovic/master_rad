package org.example;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.ws.rs.core.MediaType;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Path("/grader")
public class SolutionController {
    @POST
    @Path("/generate")
    @Consumes(MediaType.APPLICATION_JSON)
    public String generateSolution(String body){
        System.out.println("[generateSolution]");
        try {
            JSONArray arr = new JSONObject(body).getJSONArray("tasks");
            return TaskHandler.generateSolutions(arr);
        } catch (InterruptedException | IOException e) {
            return "Error occurred: " + e.getMessage();
        }
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
        System.out.println("[checkSolution]");
//        System.out.println(body);
        try {
            return TaskHandler.checkTask(body).toString();
        } catch (IOException | InterruptedException e) {
            JSONObject err = new JSONObject();
            err.put("result", false);
            err.put("message", e.getMessage());
            return err.toString();
        }
    }

    @POST
    @Path("/checkSolutionBulk")
    @Consumes(MediaType.APPLICATION_JSON)
    public String checkSolutionBulk(String body){
        System.out.println("[checkSolutionBulk]");
//        System.out.println(body);
        try {
            JSONArray arr = new JSONArray(body);
            JSONArray response = new JSONArray();
            for(int i=0; i < arr.length(); i++){
                String req = arr.getJSONObject(i).toString();
                JSONObject resp = TaskHandler.checkTask(req);
                response.put(resp);
            }
            return response.toString();
        } catch (IOException | InterruptedException e) {
            JSONObject err = new JSONObject();
            err.put("results", false);
            err.put("message", e.getMessage());
            return err.toString();
        }
    }
    @POST
    @Path("/createDatabase")
    public String createDatabase(){
        try {
            System.out.println("[createDatabase]");
            ProcessBuilder generateSolutionsPB = new ProcessBuilder("./create.sh");
            generateSolutionsPB.directory(new File("/home/scripts/stud2020"));
            Process generateSolutionP = generateSolutionsPB.start();
            TaskHandler.printConsoleResponse(generateSolutionP.getInputStream());
            TaskHandler.printConsoleResponse(generateSolutionP.getErrorStream());
            generateSolutionP.waitFor();
            generateSolutionP.destroy();
            return "Databsse created";
        } catch (IOException | InterruptedException e) {
            return "Error: " + e.getMessage();
        }
    }

}
