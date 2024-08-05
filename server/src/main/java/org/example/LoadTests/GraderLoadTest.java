package org.example.LoadTests;

import org.example.RequestHandlers;
import org.example.Tables.Submission;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.*;

public class GraderLoadTest {
    public static String GRADER_URL = "http://localhost:";
    public static void main(String[] args) throws ExecutionException, InterruptedException, IOException, NoSuchAlgorithmException {
        String fileName = "";
        Integer threadNo = -1;
        Integer requestNo = -1;
        Integer port = -1;
        Boolean generate = false;
        try {
            fileName = args[4];
            threadNo = Integer.valueOf(args[2]);
            requestNo = Integer.valueOf(args[3]);
            port = Integer.valueOf(args[1]);
            GraderLoadTest.GRADER_URL = GraderLoadTest.GRADER_URL + port;
            if (args.length>5) {
                generate = args[5].equalsIgnoreCase("gen");
            }

        } catch (Exception e){
            System.err.println("Pogresni argumenti.");
            System.err.println("Ocekuje se: test port_ocenjivaca broj_niti broj_zahteva fajl_sa_zadacima opciono_gen");
            System.out.println("Dobijeno: " + String.join(" " + args));
            System.out.println(args);
            System.out.println(args[0]);
            return;
        }

//        new LoadTesting().testGrader("correct_queries.json", 10, 10, 0);
        new GraderLoadTest().testGrader(fileName, threadNo, requestNo, 0, generate);

        Thread.sleep(500);

        System.out.println("Testing finished");
        System.exit(0);
    }

    /**
     * Sending request directly to grader
     * Not contacting server
     * Not saving anything in database
     */
    public static void testGrader(String path, Integer noThreads, Integer noRequests, Integer sleepTime, Boolean gen) throws IOException, ExecutionException, InterruptedException {
        String payloads = String.join("", Files.readAllLines(Path.of(path)));
        JSONArray arr = new JSONArray(payloads);

        if (gen)
            RequestHandlers.sendPostRequest(GraderLoadTest.GRADER_URL, RequestHandlers.GraderAction.GENERATE, arr.toString());

        ExecutorService executor = Executors.newFixedThreadPool(noThreads);

        List<Future<String>> futures = new ArrayList<>();

        Date start = new Date();
        int noSentRequests = 0;
        for (int i = 0; i < arr.length(); i++) {
            for (int j = 0; j < 100; j++) {
                Integer userId = j;
                JSONObject req = arr.getJSONObject(i);
//                req.put("userId", userId);

                Integer taskid = req.getInt("taskId");
                String query = req.getString("solution");
                String ordering = req.getString("ordering");

                final JSONObject request = Submission.prepareGraderRequest(userId, taskid, query, ordering);

                if(sleepTime > 0)
                    Thread.sleep(sleepTime);

                Future<String> future1 = executor.submit(() -> {return RequestHandlers.sendPostRequest(GraderLoadTest.GRADER_URL, RequestHandlers.GraderAction.CHECK, request.toString());});
                futures.add(future1);

                noSentRequests++;
                if (noRequests==noSentRequests)
                    break;
            }

            if (noRequests==noSentRequests)
                break;
        }

        executor.shutdown();

        Integer correct = 0;
        for (Future<String> future : futures) {
            String result = future.get();
            if (new JSONObject(result).getBoolean("ok"))
                correct++;
            System.out.println("Result: " + result);
        }
        Date end = new Date();

        executor.shutdown();

        System.out.println("Time difference " + (end.getTime() - start.getTime()));
        System.out.println("Response " + correct + "/" + noRequests);
        executor.shutdown();
    }

}
