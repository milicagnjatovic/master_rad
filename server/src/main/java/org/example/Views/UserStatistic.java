package org.example.Views;

import org.example.HibernateUtil;
import org.example.Tables.User;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.persistence.*;
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

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "USER_ID", referencedColumnName = "ID", insertable = false, updatable = false)
    public User UserForStats;

    public static List<UserStatistic> getAllStats(){
        Session session = null;
        List<UserStatistic> ret = new ArrayList<>();
        try {
            session = HibernateUtil.getSessionFactory().openSession();

            Query query = session.createQuery("FROM UserStatistic ORDER BY SuccessPercantage DESC", UserStatistic.class).setMaxResults(10);
            System.out.println("HERE1");
            ret.addAll(query.list());
            System.out.println("HERE2");
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
            obj.put("user", us.UserForStats.Username);
            obj.put("totalSubmissions", us.TotalSubmissions);
            obj.put("correctSubmissions", us.CorrectSubmissions);
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
