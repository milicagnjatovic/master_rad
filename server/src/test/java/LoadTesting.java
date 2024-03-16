import org.example.Conreollers.TaskController;
import org.example.RequestHandlers;
import org.example.Tables.Grader;
import org.example.Tables.Submission;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Time;
import java.util.*;
import java.util.concurrent.*;

public class LoadTesting {
    public static void main(String[] args) throws ExecutionException, InterruptedException, IOException {
        Grader.retrieveGradersToMap();

//        test1();

//        new LoadTesting().test("/home/milica/Desktop/master_rad/server/src/test/correct_queries.json", 5, 5);
//        new LoadTesting().test("correct_queries.json", 100, 100);
//        new LoadTesting().test("correct_queries.json", 120, 120);
//        new LoadTesting().test("correct_queries.json", 140, 140);
//        new LoadTesting().test("correct_queries.json", 50, 50);
        new LoadTesting().testGrader("correct_queries.json", 50, 15);

    }

    public void test(String path, Integer noThreads, Integer noRequests) throws ExecutionException, InterruptedException, IOException {
        String payloads = String.join("", Files.readAllLines(Path.of(path)));
        JSONArray arr = new JSONArray(payloads);
        List<Integer> userIds = Arrays.asList(22,23,24,25,26,41,42,43,44,45, 61,62,63,64,65,66,67,68,69,70);

        ExecutorService executor = Executors.newFixedThreadPool(noThreads);

        List<Future<String>> futures = new ArrayList<>();

        Date start = new Date();
        int noSentRequests = 0;
        for (int i = 0; i < arr.length(); i++) {
            for (int j = 0; j < userIds.size(); j++) {
                Integer userId = userIds.get(j);
                JSONObject req = arr.getJSONObject(i);
                req.put("userId", userId);

                final JSONObject request = new JSONObject(req, JSONObject.getNames(req));

                Future<String> future = executor.submit(() -> {
                    TaskController controller = new TaskController();
                    String ret = controller.checkTask(String.valueOf(request));
                    return ret;
                });
                futures.add(future);

                noSentRequests++;
                if (noRequests==noSentRequests)
                    break;
            }

            if (noRequests==noSentRequests)
                break;
        }

        executor.shutdown();

//        // Wait for all tasks to complete and collect their results
        for (Future<String> future : futures) {
            String result = future.get();
            System.out.println("Result: " + result);
        }
        Date end = new Date();

        executor.close();

        executor.awaitTermination(10, TimeUnit.MILLISECONDS);
        System.out.println("Time difference " + (end.getTime() - start.getTime()));
    }

    public static void testGrader(String path, Integer noThreads, Integer noRequests) throws IOException, ExecutionException, InterruptedException {
        String payloads = String.join("", Files.readAllLines(Path.of(path)));
        JSONArray arr = new JSONArray(payloads);

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
                Thread.sleep(500);
                Future<String> future = executor.submit(() -> {
                    return RequestHandlers.sendRequest("http://localhost:51000", RequestHandlers.GraderAction.CHECK, request.toString());
                });
                futures.add(future);

                noSentRequests++;
                if (noRequests==noSentRequests)
                    break;
            }

            if (noRequests==noSentRequests)
                break;
        }

        executor.shutdown();

        for (Future<String> future : futures) {
            String result = future.get();
            System.out.println("Result: " + result);
        }
        Date end = new Date();

        executor.close();

        System.out.println("Time difference " + (end.getTime() - start.getTime()));

    }
}
