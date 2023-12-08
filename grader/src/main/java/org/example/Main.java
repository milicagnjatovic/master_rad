package org.example;

import jakarta.ws.rs.core.UriBuilder;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.server.ResourceConfig;
import java.net.URI;

import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;

public class Main {
    public static void main(String[] args) throws InterruptedException {
//        URI base = UriBuilder.fromUri("http://localhost").port(51000).build();
        URI base = UriBuilder.fromUri("http://0.0.0.0").port(51000).build();
        System.out.println(base);
        ResourceConfig config = new ResourceConfig(SolutionController.class);
        HttpServer server = GrizzlyHttpServerFactory.createHttpServer(base, config);
        System.out.println(server.isStarted());

        Thread.currentThread().join();
    }
}