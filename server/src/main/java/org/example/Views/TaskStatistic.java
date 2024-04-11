package org.example.Views;


import org.example.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
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

    public static List<TaskStatistic> getAllStats(){
        Session session = null;
        List<TaskStatistic> ret = new ArrayList<>();
        try {
            session = HibernateUtil.getSessionFactory().openSession();

            Query query = session.createQuery("FROM TaskStatistic ORDER BY SuccessPercantage DESC", TaskStatistic.class);
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
            obj.put("totalSubmissions", ts.TotalSubmissions);
            obj.put("correctSubmissions", ts.TotalSubmissions);
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
