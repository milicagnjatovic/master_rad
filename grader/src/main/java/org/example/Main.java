package org.example;

import jakarta.ws.rs.core.UriBuilder;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.server.ResourceConfig;
//import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;

public class Main {
    public static void main(String[] args) throws InterruptedException, IOException {
        URI base = UriBuilder.fromUri("http://localhost").port(51000).build();
        System.out.println(base);
        ResourceConfig config = new ResourceConfig(SolutionController.class);
        HttpServer server = GrizzlyHttpServerFactory.createHttpServer(base, config);
        System.out.println(server.isStarted());

        Thread.currentThread().join();


        //        TaskHandler.generateSolutions();

//        JSONObject obj = new JSONObject();
//        obj.put("taskId", 22);
//        obj.put("solution", "select * from da.predmet");
//        obj.put("user", "mi123");
//        System.out.println(obj.toString());
//        TaskHandler.checkTask(obj.toString());

//        try {
//            String text = new String(Files.readAllBytes(Path.of("zadaci.json")));
//            System.out.println(text);
//            JSONArray arr = new JSONObject(text).getJSONArray("zadaci");
//            TaskHandler.generateSolutions(arr);
//            TaskHandler.insertNewTasks(arr);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
    }
}