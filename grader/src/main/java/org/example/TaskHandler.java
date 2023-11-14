package org.example;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class TaskHandler {
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
    public static void generateSolutions(){
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            Query<Object[]> query = session.createQuery("SELECT Id, Solution FROM Task WHERE Solution IS NOT NULL AND ID!=67"); //.setMaxResults(5);
            List<Object[]> res = query.list();

            Process connectToDatabase = new ProcessBuilder("db2", "connect to stud2020").start();
            connectToDatabase.waitFor();
            System.out.println("connected to db");

            long maxTime = 0;
            for(Object[] t : res) {
                String taskId = t[0].toString();
                String solutionQuery = t[1].toString();
                System.out.println(t[0].toString());

                long startTime = System.nanoTime();
                ProcessBuilder executeQueryPB = new ProcessBuilder("db2", solutionQuery);
                executeQueryPB.redirectOutput(new File("results/" + taskId));
                Process executeQueryP = executeQueryPB.start();
                executeQueryP.waitFor();
                executeQueryP.destroy();
                long timeDiff = (System.nanoTime() - startTime) / 1000000;
                System.out.println("Task " + taskId + " : " + timeDiff);
                maxTime = timeDiff > maxTime ? timeDiff : maxTime;
            }
            System.out.println("MAX TIME " + maxTime);
            Process disconectFromDatabase = new ProcessBuilder("db2", "connect reset").start();
            disconectFromDatabase.waitFor();

//            printConsoleResponse(disconectFromDatabase.getInputStream());

            connectToDatabase.destroy();
            disconectFromDatabase.destroy();
        } catch (Error | InterruptedException e){
            System.err.println(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            session.close();
        }

        System.out.println("DONE");
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
