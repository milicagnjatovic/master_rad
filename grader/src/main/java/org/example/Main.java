package org.example;

import jakarta.ws.rs.core.UriBuilder;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.NetworkListener;
import org.glassfish.grizzly.nio.transport.TCPNIOTransport;
import org.glassfish.grizzly.threadpool.ThreadPoolConfig;
import org.glassfish.jersey.server.ResourceConfig;

import java.io.IOException;
import java.net.URI;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;

public class Main {
    public static ThreadPoolConfig threadPoolConfig;
    public static void main(String[] args) throws InterruptedException {
//        URI base = UriBuilder.fromUri("http://localhost").port(51000).build();
        URI base = UriBuilder.fromUri("http://0.0.0.0").port(51000).build();
        System.out.println(base);
        ResourceConfig config = new ResourceConfig(SolutionController.class);

        HttpServer server = GrizzlyHttpServerFactory.createHttpServer(base, config, false);

        Collection<NetworkListener> listeners = server.getListeners();

        System.out.println("Listeners before configurations:");
        for(NetworkListener nl : listeners)
            System.out.println(nl.getName() + " : " + nl.getHost() + " : " + nl.isStarted() +
                    " : " +
                    nl.getTransport().getWorkerThreadPoolConfig().getMaxPoolSize() +
                    " : " +
                    nl.getTransport().getWorkerThreadPoolConfig().getCorePoolSize()
                    );

        NetworkListener listener = server.getListener("grizzly");

        TCPNIOTransport transport = listener.getTransport();

        Main.threadPoolConfig = transport.getWorkerThreadPoolConfig();
        threadPoolConfig.setCorePoolSize(10);
        threadPoolConfig.setMaxPoolSize(10);
        threadPoolConfig.setQueueLimit(-1); // use maximal queue limit


        try {
            server.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Listeners after configurations:");
        for(NetworkListener nl : listeners)
            System.out.println(nl.getName() + " : " + nl.getHost() + " : " + nl.isStarted() + " : " +
                    nl.getTransport().getWorkerThreadPoolConfig().getMaxPoolSize() +
                    " : " +
                    nl.getTransport().getWorkerThreadPoolConfig().getCorePoolSize()
            );

        System.out.println("Server started: " + server.isStarted());

        Thread.currentThread().join();
    }
}