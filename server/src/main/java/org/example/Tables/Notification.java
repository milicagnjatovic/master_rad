package org.example.Tables;

import org.example.HibernateUtil;
import org.hibernate.Session;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "NOTIFICATION")
public class Notification {
    @javax.persistence.Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer Id;

    @Column(name = "TITLE")
    public String Title;

    @Column(name = "TEXT")
    public String Text;

    @Column(name = "CREATED_DATE")
    public Date CreatedDate = new Date();

    public Notification(){}

    public Notification(String title, String text){
        this.Title = title;
        this.Text = text;
        this.CreatedDate = new Date();
    }

    public static List<Notification> getLastXNotifications(Integer x){
        Session session = null;

        try {
            session = HibernateUtil.getSessionFactory().openSession();

            Query query = session.createQuery("FROM Notification  ORDER BY CreatedDate DESC").setMaxResults(x);
            List<Notification> notifications = query.getResultList();
            session.close();
            return notifications;
        } catch (Error err){
            if (session.isOpen())
                session.close();
            System.out.println("err");
            return new ArrayList<>();
        }
    }

    public static JSONArray getNotificationsJSONArray(Integer x){
        List<Notification> notifications = getLastXNotifications(x);
        JSONArray ret = new JSONArray();
        for(Notification n : notifications){
            ret.put(n.toJSON());
            System.out.println(n);
            System.out.println(n.toJSON());
        }
        return ret;
    }

    public JSONObject toJSON(){
        JSONObject ret = new JSONObject();
        ret.put("id", this.Id);
        ret.put("title", this.Title);
        ret.put("text", this.Text);
        ret.put("createdDate", this.CreatedDate);
        return ret;
    }

    public static void insertNotification(Notification notification){
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();

            session.save(notification);

            session.getTransaction().commit();
            session.close();

        } catch (Exception err){
            System.err.println("error" + err.getMessage());
        } finally {
            if (session!=null && session.isOpen())
                session.close();
        }
    }

    public static void deleteNotification(Integer id){
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();

            Notification notification = session.get(Notification.class, id);
            session.delete(notification);

            session.getTransaction().commit();
            session.close();

        } catch (Exception err){
            System.err.println("error" + err.getMessage());
        } finally {
            if (session!=null && session.isOpen())
                session.close();
        }
    }

    public static void updateNotification(Integer id, String text, String title){
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();

            Notification notification = session.get(Notification.class, id);
            if (notification == null)
                return;

            if (title != null)
                notification.Title = title;

            if (text != null)
                notification.Text = text;

            session.update(notification);

            session.getTransaction().commit();
            session.close();

        } catch (Exception err){
            System.err.println("error" + err.getMessage());
        } finally {
            if (session!=null && session.isOpen())
                session.close();
        }
    }
}
