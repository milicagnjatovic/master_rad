package org.example;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.util.*;

public class TaskHandler {
//    TODO move to server
//    public static void insertNewTasks(JSONArray array){
//        int len = array.length();
//        List<Task> tasks = new ArrayList<>();
//
//
//        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
//            Transaction transaction = session.beginTransaction();
//            for (int i = 0; i < len; i++) {
//                JSONObject obj = array.getJSONObject(i);
//                Task task = new Task(obj);
//                tasks.add(task);
//                session.save(task);
//            }
////            session.persist(tasks);
//            transaction.commit();
//            System.out.println(tasks);
//        } catch (Error err) {
//            System.err.println("Error while inserting new tasks");
//        }
//    }

    private static List<TaskPayload> parseJSON(JSONArray arr) {
        List<TaskPayload> tasks = new LinkedList<>();
        for(int i=0; i< arr.length(); i++){
            JSONObject obj = arr.getJSONObject(i);
            TaskPayload task = new TaskPayload(obj);
            System.out.println(task.taskId);
//            System.out.println(task.task);
            System.out.println(task.getSolution());
            System.out.println("--------------------------------");
            tasks.add(task);
        }
        return tasks;
    }

    private static void parseJSONtoFile(JSONArray arr, File file) throws FileNotFoundException {
//        List<TaskPayload> tasks = new LinkedList<>();
        PrintStream ps = new PrintStream(file);
        for(int i=0; i< arr.length(); i++){
            JSONObject obj = arr.getJSONObject(i);
            TaskPayload task = new TaskPayload(obj);
//            System.out.println(task.id);
//            System.out.println(task.getSolution());
            ps.println(task.taskId);
            ps.println(task.getSolution());
//            System.out.println("--------------------------------");
//            tasks.add(task);
        }
        ps.flush();
        ps.close();
    }
    public static void generateSolutions(JSONArray arr) throws InterruptedException, IOException {
//        List<TaskPayload> tasks = parseJSON(arr);
        String fileName = "generate" + new Date().getTime();

        parseJSONtoFile(arr, new File(fileName));

        long startTime = System.nanoTime();

        ProcessBuilder generateSolutionsPB = new ProcessBuilder("./scripts/create_solution_v2.sh", fileName);
        Process generateSolutionP = generateSolutionsPB.start();
        printConsoleResponse(generateSolutionP.getInputStream());
        generateSolutionP.waitFor();
        generateSolutionP.destroy();

        long timeDiff = (System.nanoTime() - startTime) / 1000000;

        System.out.println("Execution time: " + timeDiff);

    }

    public static JSONObject checkTask(String request) throws IOException, InterruptedException {
        TaskPayloadUser task = new TaskPayloadUser(new JSONObject(request));

        ProcessBuilder executeCheckPB = new ProcessBuilder("./scripts/check_solution.sh", task.userId, task.taskId.toString(), task.solution);
        Process executeCheckP = executeCheckPB.start();

        String diff = new String(executeCheckP.getInputStream().readAllBytes());
//        System.out.println(ret);
        executeCheckP.waitFor();

        JSONObject ret = new JSONObject();
        if (diff.isEmpty()){
            ret.put("ok", true);
        } else if (diff.contains("--------")) {
            ret.put("ok", false);
            ret.put("checkColumns", true);
        } else {
            ret.put("ok", false);
            ret.put("checkColumns", false);
        }

        return ret;
    }

    public static void printConsoleResponse(InputStream is) throws IOException {
        Scanner sc = new Scanner(is);
        while (sc.hasNextLine())
            System.out.println(sc.nextLine());
        sc.close();
        is.close();
    }
}
