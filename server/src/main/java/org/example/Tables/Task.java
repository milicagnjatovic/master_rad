package org.example.Tables;

import org.example.HibernateUtil;
import org.hibernate.Session;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.persistence.*;
import java.util.Date;

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

    @Column(name = "CORRECT_SUBMISSIONS")
    public Integer CorrectSubmission = 0;

    @Column(name = "INCORRECT_SUBMISSIONS")
    public Integer IncorrectSubmission = 0;

    @Column(name = "LAST_GENERATED_DATE")
    public Date LastGeneratedDate = new Date();

    @Column(name = "ORDERING")
    public String Ordering;

    public Task(){}
    public Task(JSONObject obj, Grader g){
        this.Text = obj.getString("task");
        this.Solution = obj.getString("solution");
        this.Grader = g;
        this.Ordering = obj.optString("ordering", "");
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

        } catch (Error err){
            System.err.println("Error" + err.getMessage());
            JSONObject ret = new JSONObject();
            ret.put("message", "Error | " + err.getMessage());
            errorArray.put(ret);
        }
        retObj.put("errors", errorArray);
        retObj.put("tasks", responseArray);

        return retObj;
    }
}
