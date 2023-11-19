package org.example;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.Uri;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        URI base = UriBuilder.fromUri("http://localhost/").port(51000).build();
        System.out.println(base);
        ResourceConfig config = new ResourceConfig(SolutionController.class);
        GrizzlyHttpServerFactory.createHttpServer(base, config);

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