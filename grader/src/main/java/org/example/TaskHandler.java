package org.example;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.util.*;

public class TaskHandler {
//    TODO move to server
    public static void insertNewTasks(JSONArray array){
        int len = array.length();
        List<Task> tasks = new ArrayList<>();


        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            Transaction transaction = session.beginTransaction();
            for (int i=0; i<len; i++){
                JSONObject obj = array.getJSONObject(i);
                Task task = new Task(obj);
                tasks.add(task);
                session.save(task);
            }
//            session.persist(tasks);
            transaction.commit();
            System.out.println(tasks);
        } catch (Error err) {
            System.err.println("Error while inserting new tasks");
        } finally {
            session.close();
        }
    }

    private static List<TaskPayload> parseJSON(JSONArray arr) throws FileNotFoundException {
        List<TaskPayload> tasks = new LinkedList<>();
        for(int i=0; i< arr.length(); i++){
            JSONObject obj = arr.getJSONObject(i);
            TaskPayload task = new TaskPayload(obj);
            System.out.println(task.id);
            System.out.println(task.task);
            System.out.println(task.getSolution());
            System.out.println("--------------------------------");
            tasks.add(task);
        }
        return tasks;
    }

    private static void parseJSONtoFile(JSONArray arr, File file) throws FileNotFoundException {
        List<TaskPayload> tasks = new LinkedList<>();
        PrintStream ps = new PrintStream(file);
        for(int i=0; i< arr.length(); i++){
            JSONObject obj = arr.getJSONObject(i);
            TaskPayload task = new TaskPayload(obj);
//            System.out.println(task.id);
//            System.out.println(task.getSolution());
            ps.println(task.id);
            ps.println(task.getSolution());
//            System.out.println("--------------------------------");
            tasks.add(task);
        }
        ps.flush();
        ps.close();
    }
    public static void generateSolutions(JSONArray arr) throws InterruptedException, IOException {
//        List<TaskPayload> tasks = parseJSON(arr);
        String fileName = "results/generate" + new Date().getTime();

        parseJSONtoFile(arr, new File(fileName));

        long startTime = System.nanoTime();

        ProcessBuilder generateSolutionsPB = new ProcessBuilder("./scripts/create_solution_v2.sh", fileName);
        Process generateSolutionP = generateSolutionsPB.start();
        printConsoleResponse(generateSolutionP.getInputStream());
        generateSolutionP.waitFor();
        generateSolutionP.destroy();

        long timeDiff = (System.nanoTime() - startTime) / 1000000;

        System.out.println("Execution time: " + timeDiff);


//        Session session = null;
//        try {
//            session = HibernateUtil.getSessionFactory().openSession();
//            Query<Object[]> query = session.createQuery("SELECT Id, Solution FROM Task WHERE Solution IS NOT NULL AND ID!=67"); //.setMaxResults(5);
//            List<Object[]> res = query.list();
//
//            Process connectToDatabase = new ProcessBuilder("db2", "connect to stud2020").start();
//            connectToDatabase.waitFor();
//            System.out.println("connected to db");
//
//            long maxTime = 0;
//            for(Object[] t : res) {
//                String taskId = t[0].toString();
//                String solutionQuery = t[1].toString();
//                System.out.println(t[0].toString());
//
//                long startTime = System.nanoTime();
//                ProcessBuilder executeQueryPB = new ProcessBuilder("db2", solutionQuery);
//                executeQueryPB.redirectOutput(new File("results/" + taskId));
//                Process executeQueryP = executeQueryPB.start();
//                executeQueryP.waitFor();
//                executeQueryP.destroy();
//                long timeDiff = (System.nanoTime() - startTime) / 1000000;
//                System.out.println("Task " + taskId + " : " + timeDiff);
//                maxTime = timeDiff > maxTime ? timeDiff : maxTime;
//            }
//            System.out.println("MAX TIME " + maxTime);
//            Process disconectFromDatabase = new ProcessBuilder("db2", "connect reset").start();
//            disconectFromDatabase.waitFor();
//
////            printConsoleResponse(disconectFromDatabase.getInputStream());
//
//            connectToDatabase.destroy();
//            disconectFromDatabase.destroy();
//        } catch (Error | InterruptedException e){
//            System.err.println(e);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        } finally {
//            session.close();
//        }
//
//        System.out.println("DONE");
    }

    public static void checkTask(String response){
        JSONObject resp = new JSONObject(response);
        int taskId = resp.getInt("taskId");
        String solution = resp.getString("solution");
        String user = resp.getString("user");
        String outFile = "student_results/" + user + new Date().getTime();
        String correctSolution = "results/"+taskId;
        try {
//            Process pwd = new ProcessBuilder("ls").start();
//            pwd.waitFor();
//            printConsoleResponse(pwd.getInputStream());

            Process connectToDatabase = new ProcessBuilder("db2", "connect to stud2020").start();
            connectToDatabase.waitFor();
            System.out.println("connected to db");

            System.out.println(solution);
            ProcessBuilder executeQueryPB = new ProcessBuilder("db2", solution);
            executeQueryPB.redirectOutput(new File(outFile));
            Process executeQueryP = executeQueryPB.start();
            executeQueryP.waitFor();

            System.out.println(correctSolution);
            System.out.println(outFile);
            ProcessBuilder executeDiffB = new ProcessBuilder("diff", outFile, correctSolution);
//            executeDiffB.redirectOutput(new File(outFile+'_'));
            Process executeDiff = executeDiffB.start();
            printConsoleResponse(executeDiff.getInputStream());
            executeDiff.waitFor();
//            printConsoleResponse(executeDiff.getInputStream());
            System.out.println("diff executed");
            executeDiff.destroy();


            Process deleteFile = new ProcessBuilder("rm",outFile).start();
            deleteFile.waitFor();
            System.out.println("deleted file");

            Process disconectFromDatabase = new ProcessBuilder("db2", "connect reset").start();
            disconectFromDatabase.waitFor();

            printConsoleResponse(disconectFromDatabase.getInputStream());

            executeQueryP.destroy();
//            connectToDatabase.destroy();
            disconectFromDatabase.destroy();
//            deleteFile.destroy();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void printConsoleResponse(InputStream is) throws IOException {
        Scanner sc = new Scanner(is);
        while (sc.hasNextLine())
            System.out.println(sc.nextLine());
        sc.close();
        is.close();
    }
}
