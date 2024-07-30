package org.example;

import java.net.URI;

import jakarta.ws.rs.core.UriBuilder;
import org.example.Conreollers.*;
import org.example.Tables.Grader;
import org.example.Tables.Notification;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.json.JSONArray;

public class Main {
    public static void main(String[] args) {
        Grader.retrieveGradersToMap();

        URI base = UriBuilder.fromUri("http://0.0.0.0").port(5200).build();
        System.out.println(base);
        ResourceConfig config = new ResourceConfig();
        config.register(CorsFilter.class);
        config.register(TaskController.class);
        config.register(UserController.class);
        config.register(GradersController.class);
        config.register(PageController.class);
        config.register(RoleGraderPermissionController.class);
        config.register(MessageController.class);
        config.register(NotificationController.class);

        HttpServer server = GrizzlyHttpServerFactory.createHttpServer(base, config);

        System.out.println("Server running");
        JSONArray updatedNotifications = Notification.getNotificationsJSONArray(10);
        FileUtil.writeToFile(FileUtil.FILE_WITH_NOTIFICATIONS, updatedNotifications.toString());

        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}