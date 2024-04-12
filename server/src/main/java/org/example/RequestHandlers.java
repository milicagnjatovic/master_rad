package org.example;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class RequestHandlers {
    public static class GraderAction  {
        public static final String GENERATE = "/grader/generate";
        public static final String CHECK = "/grader/checkSolution";
        public static final String CHECKBULK = "/grader/checkSolutionBulk";
    }


    public static String sendPostRequest(String endpoint, String action, String body) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(endpoint +action).openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/json");
        try (OutputStream os = connection.getOutputStream()){
            os.write(body.getBytes(StandardCharsets.UTF_8));
        }

        String responseMessage = "";
        try (InputStream is = connection.getInputStream()){
            byte[] response = is.readAllBytes();
            responseMessage = new String(response, StandardCharsets.UTF_8);
            System.out.println(responseMessage);

        }

        connection.disconnect();
        return responseMessage;
    }

    public static String sendGetRequest(String endpoint, String action) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(endpoint +action).openConnection();
        connection.setRequestMethod("GET");
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/json");

        String responseMessage = "";
        try (InputStream is = connection.getInputStream()){
            byte[] response = is.readAllBytes();
            responseMessage = new String(response, StandardCharsets.UTF_8);
        }

        connection.disconnect();
        return responseMessage;
    }
}
