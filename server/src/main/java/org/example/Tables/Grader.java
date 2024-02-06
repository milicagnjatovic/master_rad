package org.example.Tables;

import org.example.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.query.Query;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.GenerationType;
import java.util.Date;
import java.util.List;
import java.util.Map;

import java.util.TreeMap;

@Entity
@Table(name = "GRADERS")
public class Grader {
    public static Map<Integer, Grader> activeGraders = new TreeMap<>();
    public static String gradersString="";

    public static void retrieveGradersToMap (){
        Session session=null;
        activeGraders.clear();
        JSONArray graderJsonArray = new JSONArray();

        session = HibernateUtil.getSessionFactory().openSession();
        Query query = session.createQuery("SELECT Id, Name, Endpoint FROM Grader WHERE Active=TRUE "); //.setMaxResults(5);
        List<Object[]> graders = query.list();
        for (Object[] obj : graders) {
            Grader grader = new Grader();
            grader.Id = (Integer) obj[0];
            grader.Name = obj[1].toString();
            grader.Endpoint = obj[2].toString();
            activeGraders.put(grader.Id, grader);

            JSONObject graderObj = new JSONObject();
            graderObj.put("id", grader.Id);
            graderObj.put("name", grader.Name);
            graderJsonArray.put(graderObj);
        }

        gradersString = graderJsonArray.toString();
    }

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer Id;

    @Column(name = "NAME", unique = true)
    public String Name;

    @Column(name = "ENDPOINT", unique = true)
    public String Endpoint;

    @Column(name = "ACTIVE")
    public boolean Active;

    @Column(name = "CREATED_DATE")
    public Date CreatedDate = new Date();

    @Override
    public String toString() {
        return this.Name + " (" + this.Id + ") " + this.Active;
    }

    public static String insertOrUpdateGrader(Grader grader){
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            session.saveOrUpdate(grader);
            session.getTransaction().commit();
            session.close();
            Grader.retrieveGradersToMap();
            return "ok";
        } catch (ConstraintViolationException err) {
            System.err.println("Error | " + err.getMessage());
            return "Error | Unique constraint error.";
        } catch (Error err) {
            return "Error | " + err.getMessage();
        }
    }

    public static Grader getById(Integer id){
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            Grader grader = session.get(Grader.class, id);
            session.close();
            return grader;
        } catch (Error err) {
            return null;
        }
    }

    public static String getAllGraders(){
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            Query query = session.createQuery("SELECT Id, Name, Endpoint, Active FROM Grader");
            List<Object[]> graders = query.list();
            JSONArray retJsonArray = new JSONArray();
            for (Object[] obj : graders) {
                Grader grader = new Grader();
                grader.Id = (Integer) obj[0];
                grader.Name = obj[1].toString();
                grader.Endpoint = obj[2].toString();
                grader.Active = (boolean) obj[3];

                JSONObject graderObj = new JSONObject();
                graderObj.put("id", grader.Id);
                graderObj.put("name", grader.Name);
                graderObj.put("endpoint", grader.Endpoint);
                graderObj.put("active", grader.Active);

                retJsonArray.put(graderObj);
            }
            return retJsonArray.toString();
        } catch (Error err) {
            return "ERROR | " + err.getMessage();
        }
    }
}
