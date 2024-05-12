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
@Table(name = "GRADER")
public class Grader {
    /**
     * Mapa aktivnih pregledača. Mapira id pregledača i pregledač. Koristi se za dohvtaanje adrese pregledača preko identifikatora.
     */
    public static Map<Integer, Grader> activeGraders = new TreeMap<>();

    /**
     * String koji sadrži JSONArray pregledača koji se šalju korisniku.
     */
    public static String gradersString="";

    /**
     * Funkcija koja popunjavam activeGraders mapu podacima iz baze.
     */
    public static void retrieveGradersToMap (){
        Session session=null;
        activeGraders.clear();
        JSONArray graderJsonArray = new JSONArray();

        session = HibernateUtil.getSessionFactory().openSession();
        Query query = session.createQuery("SELECT Id, Name, Endpoint FROM Grader WHERE Active=TRUE ");
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
        session.close();

        gradersString = graderJsonArray.toString();
    }

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer Id;

    @Column(name = "NAME", unique = true)
    public String Name;

    /**
     * Adresa na kojoj se nalazi pregledač.
     */
    @Column(name = "ENDPOINT", unique = true)
    public String Endpoint;

    /**
     * Indikator da li je pregledač aktivan. Zadaci neaktivnog pregledača ne treba da se prikazuju studetima.
     */
    @Column(name = "ACTIVE")
    public boolean Active;

    @Column(name = "CREATED_DATE")
    public Date CreatedDate = new Date();

    @Override
    public String toString() {
        return this.Name + " (" + this.Id + ") " + this.Active;
    }

    /**
     * Unosi pregledač ili ga menja u tabeli pregledača (Graders). Nakon unosa osvežava mapu pregledača.
     * @param grader
     * @return Vraća JSON unetog pregledača.
     */
    public static String insertOrUpdateGrader(Grader grader){
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            grader.CreatedDate = new Date();
            session.saveOrUpdate(grader);
            session.getTransaction().commit();
            session.close();
            Grader.retrieveGradersToMap();
            return grader.toJSON().toString();
        } catch (ConstraintViolationException err) {
            System.err.println("Error | " + err.getMessage());
            return new JSONObject().put("error", "Name is already taken").toString();
        } catch (Exception err) {
            return new JSONObject().put("error", err.getMessage()).toString();
        } finally {
            if (session!=null && session.isOpen())
                session.close();
        }
    }

    /**
     * Dohvata pregledač iz tabele po id-u
     * @param id pregledača
     * @return Vraća dohvaćeni pregledač ili null ukoliko ne postoji.
     */
    public static Grader getById(Integer id){
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            Grader grader = session.get(Grader.class, id);
            session.close();
            return grader;
        } catch (Exception err) {
            return null;
        } finally {
            if (session!=null && session.isOpen())
                session.close();
        }
    }

    public Grader(Integer id){
        this.Id = id;
    }

    public Grader(){}

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

                JSONObject graderObj = grader.toJSON();

                retJsonArray.put(graderObj);
            }
            return retJsonArray.toString();
        } catch (Exception err) {
            return new JSONObject().put("error", err.getMessage()).toString();
        } finally {
            if (session!=null && session.isOpen())
                session.close();
        }
    }

    public JSONObject toJSON(){
        JSONObject graderObj = new JSONObject();
        graderObj.put("id", this.Id);
        graderObj.put("name", this.Name);
        graderObj.put("endpoint", this.Endpoint);
        graderObj.put("active", this.Active);
        return graderObj;
    }
}
