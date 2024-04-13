package org.example;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class RequestHandlers {

    /**
     * Klasa sa konstantama za krajeve putanja na koje se šalju zahtevi pregledaču.
     */
    public static class GraderAction  {
        /**
         * Putanja za generisanja fajla sa rešenjem zadatka na serveru.
         */
        public static final String GENERATE = "/grader/generate";
        /**
         * Putanja za proveru studentskog rešenja.
         */
        public static final String CHECK = "/grader/checkSolution";
        /**
         * Putanja za slanje više radova na pregledanje.
         */
        public static final String CHECKBULK = "/grader/checkSolutionBulk";
    }

    /**
     * Funkcija za slanje post zahteva. Zahtev se šalje na lokaciju endpoint+action. Endpoint se generalno dobija iz gradera.
     * @param endpoint - adresa na koju se šalje zahteb, eg localhost:50000
     * @param action - ostatak putanje na koju se šalje zahtev, eg /grader/checkSolution
     * @param body - telo zahteva koji treba da se pošalje
     * @return - Funkcija vraća odgovor servera kom se šalje zahtev.
     * @throws IOException
     */
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

    /**
     * Funkcija koja šalje get zahtev na putanju endpoint+action
     * @param endpoint - adresa na koju se šalje zahteb, eg localhost:50000
     * @param action - ostatak putanje na koju se šalje zahtev, eg /grader/checkSolution
     * @return - Funkcija vraća odgovor servera kom se šalje zahtev.
     * @throws IOException
     */
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
