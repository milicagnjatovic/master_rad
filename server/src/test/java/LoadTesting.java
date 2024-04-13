import org.example.Conreollers.TaskController;
import org.example.HibernateUtil;
import org.example.RequestHandlers;
import org.example.Tables.Grader;
import org.example.Tables.Submission;
import org.example.Tables.User;
import org.hibernate.Session;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.*;

public class LoadTesting {
    public static String GRADER_URL = "http://localhost:51000";
    public static String SERVER_URL = "http://localhost:52000";
    public static void main(String[] args) throws ExecutionException, InterruptedException, IOException, NoSuchAlgorithmException {
//        Grader.retrieveGradersToMap();

        new Thread(()->{
            for (Integer i=0; i<10; i++) {
                try { Thread.sleep(10);
                } catch (InterruptedException e) { throw new RuntimeException(e); }
                getPageServer("getAllTasks");
            }
        }).start();

        new Thread(()->{
            for (Integer i=0; i<10; i++) {
                try { Thread.sleep(10);
                } catch (InterruptedException e) { throw new RuntimeException(e); }
                getPageServer("getStats");
            }
        }).start();


        new LoadTesting().testServer("correct_queries.json", 100, 100);

//            new LoadTesting().testGrader("correct_queries.json", 10, 10, 0);


        Thread.sleep(500);

//        Session session = null;
//        try {
//            session = HibernateUtil.getSessionFactory().openSession();
//            session.beginTransaction();
//
//            session.createQuery("delete from User where Username LIKE 'test%'").executeUpdate();
//
//            session.getTransaction().commit();
//            session.close();
//        } catch (Error err){
//            if (session.getTransaction().isActive())
//                session.getTransaction().rollback();
//            if (session.isOpen())
//                session.close();
//            System.err.println(err.getMessage());
//        }

        System.out.println(Runtime.getRuntime().availableProcessors());
        System.out.println("OK");
        System.exit(0);
    }


    /**
     * Sending request to server
     * Saving to database
     */
    public void testServer(String path, Integer noThreads, Integer noRequests) throws ExecutionException, InterruptedException, IOException, NoSuchAlgorithmException {
        String payloads = String.join("", Files.readAllLines(Path.of(path)));
        JSONArray arr = new JSONArray(payloads);

        Integer noTasksToUse = arr.length();
        Integer noUsers = (int) Math.ceil(noRequests * 1.0 / noTasksToUse);
        new LoadTesting();
        List<User> testUsers = createTestUsers(noUsers, 100);


        ExecutorService executor = Executors.newFixedThreadPool(noThreads);

        List<Future<String>> futures = new ArrayList<>();

        Date start = new Date();
        int noSentRequests = 0;
        for (int i = 0; i < arr.length(); i++) {
            for (int j = 0; j < testUsers.size(); j++) {
                Integer userId = testUsers.get(j).Id;
                JSONObject req = arr.getJSONObject(i);
                req.put("userId", userId);

                final JSONObject request = new JSONObject(req, JSONObject.getNames(req));

                Future<String> future = executor.submit(() -> { return RequestHandlers.sendPostRequest("http://localhost:52000", "/server/checkTask", request.toString());});

                futures.add(future);

                noSentRequests++;
                if (noRequests==noSentRequests)
                    break;
            }

            if (noRequests==noSentRequests)
                break;
        }

        executor.shutdown();

//         Wait for all tasks to complete and collect their results
        Integer correct = 0;
        for (Future<String> future : futures) {
            String result = future.get();
            System.out.println("Result: " + result);
            if (new JSONObject(result).getBoolean("ok"))
                correct++;
        }
        Date end = new Date();

        executor.shutdown();

        executor.awaitTermination(10, TimeUnit.MILLISECONDS);
        System.out.println("Time difference " + (end.getTime() - start.getTime()));
        System.out.println("Result " + correct + "/" + noRequests);
    }

/**
 * Sending request directly to grader
 * Not contacting server
 * Not saving anything in database
 */
    public static void testGrader(String path, Integer noThreads, Integer noRequests, Integer sleepTime) throws IOException, ExecutionException, InterruptedException {
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

                if(sleepTime > 0)
                    Thread.sleep(sleepTime);

                Future<String> future1 = executor.submit(() -> {return RequestHandlers.sendPostRequest("http://localhost:51000", RequestHandlers.GraderAction.CHECK, request.toString());});
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

    /**
     * Send requests to create noUsers
     * returns list of created users
     * requests are sent in parallel using noThreads threads
     */
    public static List<User> createTestUsers(Integer noUsers, Integer noThreads) throws ExecutionException, InterruptedException, NoSuchAlgorithmException {
        List<User> users = new ArrayList<>();

        ExecutorService executor = Executors.newFixedThreadPool(noThreads);
        List<Future<String>> futures = new ArrayList<>();

        for (int i = 0; i < noUsers; i++) {
            User user = new User();
            user.Email = "test" + i + "@gmail.com";
            user.Username = "test" + i;
            user.Password = "12345";

            JSONObject request = user.toJSON(user.Password);

            Future<String> future = executor.submit(() -> {return RequestHandlers.sendPostRequest(SERVER_URL, "/user/create", request.toString());});
            futures.add(future);
        }


        Integer noCreatedUsers = 0;
        for (Future<String> future : futures) {
            String result = future.get();
            if (new JSONObject(result).has("Error"))
                continue;
            User user = new User(new JSONObject(result));
            users.add(user);
            System.out.println("Result: " + result);
        }

        System.out.println("No created users: " + users.size());

        return users;
    }


    public static void getPageServer(String action){
        try {
            String response = RequestHandlers.sendGetRequest(SERVER_URL, "/page/" + action);
            System.out.println("[" +  action+ "]" + response);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
