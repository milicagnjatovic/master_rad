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

    /**
     * Pregledač zadužen za pregledanje datog zadatka
     */
    @ManyToOne
    @JoinColumn(name = "GRADER_ID", referencedColumnName = "ID", nullable = false)
    public Grader Grader;

    /**
     * Tekst zadatka
     */
    @Column(name = "TEXT", nullable = false)
    public String Text;

    /**
     * Upit koji predstavlja tačno rešenje zadatka
     */
    @Column(name = "SOLUTION", nullable = false)
    public String Solution;

    @Column(name = "LAST_GENERATED_DATE")
    public Date LastGeneratedDate = new Date();

    /**
     * Sortiranje potrebno pregledaču za pregledanje
     */
    @Column(name = "ORDERING")
    public String Ordering;

    public Task(){}

    /**
     * Konstruktor koji kreira task na osnovu JSON objekta koji se dobija u zahtevu
     * @param obj JSON objekat koji treba da bude narednog formata:
    <pre>
    {
        "taskId": 123, // potrebno ukoliko se zadatak menja
        "task": "tekst zadatka",
        "solution": "upit koji predstavlja tačno rešenje",
        "ordering": "1"
    }
    </pre>
     * @param grader - pregledač zadužen za prosleđeni zadatak
     */
    public Task(JSONObject obj, Grader grader){
        if (obj.has("task"))
            this.Text = obj.getString("task");

        if(obj.has("taskId"))
            this.Id = obj.getInt("taskId");

        this.Solution = obj.optString("solution", null);
        this.Grader = grader;
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

    /**
     * Funkcija koja unosi nove zadatke u tabelu zadataka (Taks)
     * @param tasks niz zaataka koji se unose u bazu
     * @param grader grader za koji su namenjeni zadaci
     * @return vraća JSON objekat u narednom formatu
     <pre>
    {
        "errors":  [
            { "message", "Error | Missing task or solution for task no. " + redniBrojZadatka }
        ]
        "tasks": [
            {
            "taskId": 2, // id unetog zadatka
            "solution", "...", // upit koji je tačno rešenje zadatka
            "ordering", "1,2" // sortiranje potrebno pregledaču za pregleanju
            }
        ]
    }
    </pre>
     */
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

    /**
     * Funkcija za izmenu zadataka u tabeli, na primer teksta zadatka ili rešenja,
     * @param tasks niz zadataka koje treba izmeniti
     * @param grader pregledač zadužen za pregledanja
     * @return vraća JSON objekat u narednom formatu
 *      <pre>
 *     {
 *         "errors":  [
 *             { "message", "Error | Missing task or solution for task no. " + redniBrojZadatka }
 *         ]
 *         "tasks": [
 *             {
 *             "taskId": 2, // id unetog zadatka
 *             "solution", "...", // upit koji je tačno rešenje zadatka
 *             "ordering", "1,2" // sortiranje potrebno pregledaču za pregleanju
 *             }
 *         ]
 *     }
 *     </pre>
     */
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
            ret.put("error", "Error | " + err.getMessage());
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

    /**
     * Funkcija koja dohvata zadatke za aktivne pregledače
     * @return Funkcija vraća niz zadataka
     */
    public static Map<Integer, List<Task>> getAllTasksPerRole(){
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            Query queryTasks = session.createQuery("FROM Task WHERE Grader.Active = true", Task.class);
            List<Task> tasks = queryTasks.list();

            Map<Integer, List<Task>> tasksPerGrader = new HashMap<>();
            for (Task task: tasks){
                if (tasksPerGrader.containsKey(task.Grader.Id)){
                    tasksPerGrader.get(task.Grader.Id).add(task);
                } else {
                    List tmpTask = new ArrayList();
                    tmpTask.add(task);
                    tasksPerGrader.put(task.Grader.Id, tmpTask);
                }
            }

            Query queryPermissions = session.createQuery("FROM RoleGraderPermission ", RoleGraderPermission.class);
            List<RoleGraderPermission> permissions = queryPermissions.list();

            Map<Integer, List<Task>> tasksPerRole = new HashMap<>();
            for (RoleGraderPermission permission : permissions){
                if(!tasksPerGrader.containsKey(permission.PermissionId.GraderId)){
                    System.out.println("Grader does not have tasks");
                    continue;
                }

                if (tasksPerRole.containsKey(permission.PermissionId.RoleId)){
                    tasksPerRole.get(permission.PermissionId.RoleId).addAll(tasksPerGrader.get(permission.PermissionId.GraderId));
                } else {
                    List<Task> tasksForRole = new ArrayList<>();
                    tasksForRole.addAll(tasksPerGrader.get(permission.PermissionId.GraderId));
                    tasksPerRole.put(permission.PermissionId.RoleId, tasksForRole);
                }
            }

            session.close();
            return tasksPerRole;
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
