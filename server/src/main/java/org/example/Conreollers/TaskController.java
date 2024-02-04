package org.example.Conreollers;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Path("/server")
public class TaskController {
    public static String STUD2020GRADER = "http://localhost:51000/grader/";

    private static String sendRequest(String action, String body) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(STUD2020GRADER+action).openConnection();
        connection.setRequestMethod(POST);
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/json");
        try (OutputStream os = connection.getOutputStream()){
            os.write(body.getBytes(StandardCharsets.UTF_8));
        }

        int responseCode = connection.getResponseCode();
        System.out.println("Response code " + responseCode);
        String responseMessage = "";
        try (InputStream is = connection.getInputStream()){
            byte[] response = is.readAllBytes();
            responseMessage = new String(response, StandardCharsets.UTF_8);
            System.out.println(responseMessage);

        }

        connection.disconnect();
        return responseMessage;
    }

    public static String POST = "POST";
    @POST
    @Path("/addTasks")
    @Consumes(MediaType.APPLICATION_JSON)
    public String addTasks(String body){
        System.out.println("[add task]");
//        System.out.println(body);

        Session session = null;
        try {
            JSONArray arr = new JSONArray(body);
            for(int i=0; i < arr.length(); i++){

            }


        } catch (Error e) {
            return "ERROR | " + e.getMessage();
        }
        return "ok";
    }

    @POST
    @Path("/checkTask")
    @Consumes(MediaType.APPLICATION_JSON)
    public String checkTask(String body){
        System.out.println("[check task]");
//        System.out.println(body);

        try {
            String response = sendRequest("checkSolution", body);
            return response;
        } catch (IOException e) {
            return "ERROR | " + e.getMessage();
        }
    }

    @POST
    @Path("/checkTaskBulk")
    @Consumes(MediaType.APPLICATION_JSON)
    public String checkTaskBulk(String body){
        System.out.println("[check task bulk]");
        System.out.println(body);
        try {
            String response = sendRequest("checkSolutionBulk", body);
            return response;
        } catch (IOException e) {
            return "ERROR | " + e.getMessage();
        }
    }

    @POST
    @Path("/updateTasks")
    @Consumes(MediaType.APPLICATION_JSON)
    public String updateTask(String body){
        System.out.println("[check task bulk]");
//        System.out.println(body);
//        TODO update task in database
        try {
            String response = sendRequest("generate", body);
            return response;
        } catch (IOException e) {
            return "ERROR | " + e.getMessage();
        }
    }
}
