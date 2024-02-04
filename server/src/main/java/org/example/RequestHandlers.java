package org.example;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class RequestHandlers {
    public static class GraderAction  {
        public static String GENERATE = "/generate";
        public static String CHECK = "/checkSolution";
        public static String CHECKBULK = "/checkSolutionBulk";
    }

    public static String sendRequest(String endpoint, String action, String body) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(endpoint+ "/grader" +action).openConnection();
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
}
