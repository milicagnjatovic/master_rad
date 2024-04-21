package org.example;

import java.net.URI;
import jakarta.ws.rs.core.UriBuilder;
import org.example.Conreollers.*;
import org.example.Tables.Grader;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

public class Main {
    public static void main(String[] args) {
        Grader.retrieveGradersToMap();

        URI base = UriBuilder.fromUri("http://0.0.0.0").port(52000).build();
        System.out.println(base);
        ResourceConfig config = new ResourceConfig();
        config.register(TaskController.class);
        config.register(UserController.class);
        config.register(GradersController.class);
        config.register(PageController.class);
        config.register(RoleGraderPermissionController.class);
        HttpServer server = GrizzlyHttpServerFactory.createHttpServer(base, config);

        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}