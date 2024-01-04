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
    public static String generateSolutions(JSONArray arr) throws InterruptedException, IOException {
        String fileName = "generate" + new Date().getTime();

        parseJSONtoFile(arr, new File(fileName));

        ProcessBuilder generateSolutionsPB = new ProcessBuilder("bash", "./scripts/create_solution_v2.sh", fileName);
        Process generateSolutionP = generateSolutionsPB.start();
        String response = new String(generateSolutionP.getInputStream().readAllBytes());
        printConsoleResponse(generateSolutionP.getErrorStream());

        generateSolutionP.waitFor();
        generateSolutionP.destroy();

        return response;
    }

    public static JSONObject checkTask(String request) throws IOException, InterruptedException {
        TaskPayloadUser task = new TaskPayloadUser(new JSONObject(request));

        ProcessBuilder executeCheckPB = new ProcessBuilder("bash", "./scripts/check_solution.sh", task.requestId, task.taskId.toString(), task.solution);
        Process executeCheckP = executeCheckPB.start();

        String diff = new String(executeCheckP.getInputStream().readAllBytes());
        printConsoleResponse(executeCheckP.getErrorStream());
        executeCheckP.waitFor();

//        System.out.println(diff);

        if (diff.isEmpty()){
            return createReturnObject(task.requestId, null);
        }else {
            return createReturnObject(task.requestId, diff);
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
