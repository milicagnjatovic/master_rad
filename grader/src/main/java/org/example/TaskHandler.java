package org.example;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.util.*;

public class TaskHandler {
    private static List<TaskPayload> parseJSON(JSONArray arr) {
        List<TaskPayload> tasks = new LinkedList<>();
        for(int i=0; i< arr.length(); i++){
            JSONObject obj = arr.getJSONObject(i);
            TaskPayload task = new TaskPayload(obj);
            System.out.println(task.taskId);
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
            ps.println(task.taskId);
            ps.println(task.getSolution());
        }
        ps.flush();
        ps.close();
    }
    public static void generateSolutions(JSONArray arr) throws InterruptedException, IOException {
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

        ProcessBuilder executeCheckPB = new ProcessBuilder("./scripts/check_solution.sh", task.requestId, task.taskId.toString(), task.solution);
        Process executeCheckP = executeCheckPB.start();

        String diff = new String(executeCheckP.getInputStream().readAllBytes());
        executeCheckP.waitFor();

//        System.out.println(diff);

        if (diff.isEmpty()){
            return createReturnObject(task.requestId, null);
        } else if (diff.contains("--------")) {
            return createReturnObject(task.requestId, "Check columns.");
        } else if (diff.startsWith("E100")) {
            return createReturnObject(task.requestId, "Server error. File creation failed.");
        } else if(diff.startsWith("E200")){
            return createReturnObject(task.requestId, "Check syntax");
        } else if(diff.startsWith("E300")){
            return createReturnObject(task.requestId, "Server error. File with result is mising.");
        }else {
            return createReturnObject(task.requestId, "Wrong query");
        }
    }

    public static JSONObject createReturnObject(String requestId, String message){
        JSONObject ret = new JSONObject();
        ret.put("requestId", requestId);
        if (message == null){
            ret.put("ok", true);
            return ret;
        }
        ret.put("ok", false);
        ret.put("message", message);
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
