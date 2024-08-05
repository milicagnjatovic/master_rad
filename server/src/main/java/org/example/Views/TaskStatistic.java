package org.example.Views;


import org.example.HibernateUtil;
import org.example.Tables.Task;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "TASK_STATS")
public class TaskStatistic {
    @Id
    @Column(name = "TASK_ID")
    public Integer TaskId;

    @Column(name = "TOTAL")
    public  Integer TotalSubmissions;

    @Column(name = "CORRECT")
    public  Integer CorrectSubmissions;

    @Column(name = "PERCENT")
    public double SuccessPercantage;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "TASK_ID", referencedColumnName = "ID", insertable = false, updatable = false)
    public Task TaskForStats;

    public static List<TaskStatistic> getAllStats(){
        Session session = null;
        List<TaskStatistic> ret = new ArrayList<>();
        try {
            session = HibernateUtil.getSessionFactory().openSession();

            Query query = session.createQuery("FROM TaskStatistic ORDER BY SuccessPercantage DESC", TaskStatistic.class).setMaxResults(10);
            ret.addAll(query.list());
            session.close();
            return ret;
        } catch (Error err){
            if(session != null && session.isOpen())
                session.close();
            System.out.println("Error | " + err.getMessage());
            return ret;
        }
    }

    public static JSONArray getJSONfromList(List<TaskStatistic> stats){
        JSONArray arr = new JSONArray();

        for (TaskStatistic ts : stats){
            JSONObject obj = new JSONObject();
            obj.put("taskId", ts.TaskId);
            obj.put("task", ts.TaskForStats.Name);
            obj.put("totalSubmissions", ts.TotalSubmissions);
            obj.put("correctSubmissions", ts.CorrectSubmissions);
            obj.put("successPercantage", ts.SuccessPercantage);
            arr.put(obj);
        }

        return arr;
    }

    public static String refreshTaskStatsFile(){
        List<TaskStatistic> stats = TaskStatistic.getAllStats();
        JSONArray json = TaskStatistic.getJSONfromList(stats);
        return json.toString();
    }

}
