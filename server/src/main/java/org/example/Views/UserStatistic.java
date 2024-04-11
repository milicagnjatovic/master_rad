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
@Table(name = "USER_STATS")
public class UserStatistic {
    @Id
    @Column(name = "USER_ID")
    public Integer UserId;

    @Column(name = "TOTAL")
    public  Integer TotalSubmissions;

    @Column(name = "CORRECT")
    public  Integer CorrectSubmissions;

    @Column(name = "PERCENT")
    public double SuccessPercantage;

    public static List<UserStatistic> getAllStats(){
        Session session = null;
        List<UserStatistic> ret = new ArrayList<>();
        try {
            session = HibernateUtil.getSessionFactory().openSession();

            Query query = session.createQuery("FROM UserStatistic ORDER BY SuccessPercantage DESC", UserStatistic.class);
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

    public static JSONArray getJSONfromList(List<UserStatistic> stats){
        JSONArray arr = new JSONArray();

        for (UserStatistic us : stats){
            JSONObject obj = new JSONObject();
            obj.put("userId", us.UserId);
            obj.put("totalSubmissions", us.TotalSubmissions);
            obj.put("correctSubmissions", us.TotalSubmissions);
            obj.put("successPercantage", us.SuccessPercantage);
            arr.put(obj);
        }

        return arr;
    }

    public static String refreshUserStatsFile(){
        List<UserStatistic> stats = UserStatistic.getAllStats();
        JSONArray json = UserStatistic.getJSONfromList(stats);
        return json.toString();
    }

}
