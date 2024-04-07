package org.example.Tables;

import org.example.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "TASKS")
public class Task {
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer Id;

    @ManyToOne
    @JoinColumn(name = "GRADER_ID", referencedColumnName = "ID", nullable = false)
    public Grader Grader;

    @Column(name = "TEXT", nullable = false)
    public String Text;

    @Column(name = "SOLUTION", nullable = false)
    public String Solution;

    @Column(name = "LAST_GENERATED_DATE")
    public Date LastGeneratedDate = new Date();

    @Column(name = "ORDERING")
    public String Ordering;

    public Task(){}
    public Task(JSONObject obj, Grader g){
        if (obj.has("task"))
            this.Text = obj.getString("task");

        if(obj.has("taskId"))
            this.Id = obj.getInt("taskId");

        this.Solution = obj.optString("solution", null);
        this.Grader = g;
        this.Ordering = obj.optString("ordering", null);
    }

    public Task(Integer id){
        this.Id = id;
    }

    public Task(Integer id, String ordering, Integer graderId){
        this.Id = id;
        this.Grader = new Grader(graderId);
        this.Ordering = ordering;
    }

    @Override
    public String toString() {

        return this.Grader.Id + " " + this.Id + "\n" +
                this.Text + "\n" + this.Ordering + "\n" + this.Solution;
    }

    public static JSONObject insertTasks(JSONArray tasks, Grader grader){
        JSONArray responseArray = new JSONArray();
        JSONArray errorArray = new JSONArray();
        Session session = null;
        JSONObject retObj = new JSONObject();
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            for (int i = 0; i < tasks.length(); i++) {
                JSONObject taskObject = tasks.getJSONObject(i);
                if (!taskObject.has("task") || !taskObject.has("solution")) {
                    JSONObject obj = new JSONObject();
                    obj.put("message", "Error | Missing task or solution for task no. " + i );
                    errorArray.put(obj);
                    continue;
                }
                Task task = new Task(taskObject, grader);
                Integer id = (Integer) session.save(task);

                JSONObject obj = new JSONObject();
                obj.put("taskId", id);
                obj.put("solution", task.Solution);
                obj.put("ordering", task.Ordering);
                responseArray.put(obj);

            }
            session.getTransaction().commit();
            session.close();

        } catch (Exception err){
            System.err.println("Error" + err.getMessage());
            JSONObject ret = new JSONObject();
            ret.put("message", "Error | " + err.getMessage());
            errorArray.put(ret);
        } finally {
            if (session!=null && session.isOpen())
                session.close();
        }
        retObj.put("errors", errorArray);
        retObj.put("tasks", responseArray);

        return retObj;
    }

    public static JSONObject updateTasks(JSONArray tasks, Grader grader){
        JSONArray responseArray = new JSONArray();
        JSONArray errorArray = new JSONArray();
        Session session = null;
        JSONObject retObj = new JSONObject();

        try {
            session = HibernateUtil.getSessionFactory().openSession();

            Map<Integer, Task> taskIdToTask = new HashMap<>();
            for (int i = 0; i < tasks.length(); i++) {
                JSONObject taskObject = tasks.getJSONObject(i);
                if (!taskObject.has("taskId")) {
                    JSONObject obj = new JSONObject();
                    obj.put("message", "Error | Missing taskId for task no. " + i );
                    errorArray.put(obj);
                    continue;
                }
                Task task = new Task(taskObject, grader);
                taskIdToTask.put(task.Id, task);
            }

            Query<Task> query = session.createQuery("from Task where Id in :ids");
            query.setParameter("ids", taskIdToTask.keySet());
            List<Task> tasksForUpdate = query.list();

            System.out.println("Tasks for update " + tasksForUpdate);

            session.beginTransaction();

            for(Task task : tasksForUpdate){
                Task newTask = taskIdToTask.get(task.Id);
                if (newTask.Solution != null && !newTask.Solution.isBlank())
                    task.Solution = newTask.Solution;

                if (newTask.Text != null && !newTask.Text.isBlank())
                    task.Text = newTask.Text;

                if (newTask.Ordering != null && !newTask.Ordering.isBlank())
                    task.Ordering = newTask.Ordering;

                task.LastGeneratedDate = new Date();

                session.update(task);

                JSONObject obj = new JSONObject();
                obj.put("taskId", task.Id);
                obj.put("solution", task.Solution);
                obj.put("ordering", task.Ordering);
                responseArray.put(obj);
            }

            session.getTransaction().commit();
            session.close();

        } catch (Exception err){
            System.err.println("Error " + err.getMessage() + err.getCause() + err.getLocalizedMessage());
            JSONObject ret = new JSONObject();
            ret.put("message", "Error | " + err.getMessage());
            errorArray.put(ret);
        } finally {
            if (session!=null && session.isOpen())
                session.close();
        }
        retObj.put("errors", errorArray);
        retObj.put("tasks", responseArray);

        return retObj;
    }

    public static Task getById(Integer id){
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            Task task = session.get(Task.class, id);
            session.close();
            return task;
        } catch (Exception err) {
            return null;
        } finally {
            if (session!=null && session.isOpen())
                session.close();
        }
    }

    public static List<Task> getAllTasks(){
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            Query query = session.createQuery("FROM Task WHERE Grader.Active = true", Task.class);
            List<Task> tasks = query.list();
            session.close();
            return tasks;
        } catch (Exception err) {
            return null;
        } finally {
            if (session!=null && session.isOpen())
                session.close();
        }
    }

    public static JSONArray tasksToJSONArray(List<Task> tasks){
        JSONArray ret = new JSONArray();

        for (Task task : tasks){
            JSONObject obj = new JSONObject();
            obj.put("taskId", task.Id);
            obj.put("text", task.Text);
            obj.put("graderId", task.Grader.Id);
            ret.put(obj);
        }

        return ret;
    }

}
