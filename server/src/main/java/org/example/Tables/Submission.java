package org.example.Tables;

import org.example.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "SUBMISSIONS")
public class Submission {
    @EmbeddedId
    public SubmissionID SubmissionId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "TASK_ID", referencedColumnName = "ID", insertable = false, updatable = false)
    public org.example.Tables.Task Task;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "USER_ID", referencedColumnName = "ID", insertable = false, updatable = false)
    public org.example.Tables.User User;

    /**
     * Poslednje rešenja zadatka koje je korisnik poslao.
     */
    @Column(name = "QUERY")
    public String Query;

    /**
     * Da li je poslednje poslato rešenje tačno.
     */
    @Column(name = "IS_CORRECT")
    public Boolean IsCorrect = false;

    /**
     * Poruka pregledača za poslednje poslato rešenje. Ako je rešenje tačno biće vreme izvršavanja, inače poruka o gešci.
     */
    @Column(name = "MESSAGE")
    public String Message;

    @Column(name = "LAST_UPDATE_TIME")
    public Date LastUpdateTime = new Date();

    /**
     * Indikator da li se čeka odgovor pregledača za poslednje poslato rešenje. Ukoliko se čeka odgovor korisnik neće moći da šalje novo rešenje na pregledanje.
     */
    @Column(name = "WAITING_FOR_RESPONSE")
    public boolean WaitingForResponse = false;

    /**
     * Broj tačno polatih rešenja datog zadatka za datog studenta.
     */
    @Column(name = "NO_CORRECT_SUBMISSIONS")
    public Integer CorrectSubmissions = 0;

    /**
     * Ukupan broj rešenja koje je dati korisnik poslao za dati zadatak.
     */
    @Column(name = "NO_SUBMISSIONS")
    public Integer TotalSubmissions = 0;

    @Override
    public String toString() {
        return Task.Id + " " + User.Id + " " + this.Query;
    }

    public JSONObject toJSON(){
        JSONObject obj = new JSONObject();
        obj.put("taskId", this.Task.Id);
        obj.put("noCorrect", this.CorrectSubmissions);
        obj.put("noTotalSubmissions", this.TotalSubmissions);
        obj.put("isWaitingForResponse", this.WaitingForResponse);
        return obj;
    }

    /**
     *
     * @param id submissiona
     * @return Funkcija vraća submission, odnosno objekat za određenog studenta i određeni zadatak.
     */
    public static Submission getById (SubmissionID id){
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            System.out.println(id);
            Submission submission =  session.get(Submission.class, id);
            session.close();
            return submission;
        } catch (Exception err) {
            return null;
        } finally {
            if (session!=null && session.isOpen())
                session.close();
        }
    }

    /**
     * Funkcija koja priprema telo zahteva koji se šalje pregledaču za pregledanje rada.
     * @param userId id korisnika
     * @param taskId id zadatka za pregledanje
     * @param query upit koji treba pregledati
     * @param ordering ukoliko je potrebno izvršiti sortiranje pri pregledanju, dohvata se iz zadatka, a potrebno je pregledaču za pregledanje
     * @return Funkcija vrća JSON objekat koji treba iskoristiit u telu zahteva
     */
    public static JSONObject prepareGraderRequest(Integer userId, Integer taskId, String query, String ordering){
        System.out.println("[prepareGraderRequest]");
        JSONObject graderRequest = new JSONObject();
        graderRequest.put("requestId", taskId + "-" + userId);
        graderRequest.put("taskId", taskId);
        graderRequest.put("solution", query);
        if (ordering != null && !ordering.isEmpty())
            graderRequest.put("ordering", ordering);
        return graderRequest;
    }

    /**
     * Funkcija koja šalje više zadataka za pregledanje odjednom.
     * @param submissions Niz submissiona koje treba pregledati
     * @return Funkcija vraća mapu koja za ključ ima id pregledača, a vrednost je JSONArray sa submissionima koje treba pregledati
     */
    public static Map<Integer, JSONArray> prepareGraderBulkRequest(List<Submission> submissions){
        Map<Integer, JSONArray> submissionPerGrader = new HashMap<>();

        for (Submission submission : submissions){
            Integer graderId = submission.Task.Grader.Id;
            JSONObject preparedRequests = Submission.prepareGraderRequest(submission.User.Id, submission.Task.Id, submission.Query, submission.Task.Ordering);
            if (submissionPerGrader.containsKey(graderId)){
                submissionPerGrader.get(graderId).put(preparedRequests);
            } else {
                JSONArray array = new JSONArray();
                array.put(preparedRequests);
                submissionPerGrader.put(graderId, array);
            }
        }

        return submissionPerGrader;
    }

    public Submission(){};

    public Submission(Integer userId, Integer taskId, boolean isCorrect, String message){
        this.SubmissionId = new SubmissionID(userId, taskId);
        this.Message = message;
        this.IsCorrect = isCorrect;
    }

    public Submission(SubmissionID id){
        this.SubmissionId = id;
    }

    /**
     * Funkcija koja dohvata iz baze zadatke koji čekaju na pregledanje.
     * @return Funkcija vraća niz submission-a koji čekaju na pregledanje
     */
    public static List<Submission> getSubmissionsWaiting(){
        System.out.println("[getSubmissionsWaiting]");
        Session session = null;
        try{
            List<Submission> ret = new ArrayList<>();
            session = HibernateUtil.getSessionFactory().openSession();
            Query query = session.createQuery("SELECT s.User.Id, s.Task.Id, s.Task.Grader.Id, s.Task.Ordering, s.Query FROM Submission s WHERE s.WaitingForResponse=TRUE");
            List<Object[]> submissions = query.list();
            for (Object[] obj : submissions) {
                Submission submission = new Submission();
                Integer userId = (Integer) obj[0];
                submission.User = new User(userId);
                Integer taskId = (Integer) obj[1];
                Integer taskGrader = (Integer) obj[2];
                String taskOrdering = obj[3]==null ? "" : obj[3].toString();
                submission.Task = new Task(taskId, taskOrdering, taskGrader);
                submission.Query = obj[4].toString();

                ret.add(submission);
            }
            session.close();
            return ret;
        } catch (Exception err) {
            System.out.println("Error | " + err.getMessage());
            return new ArrayList<Submission>();
        } finally {
            if (session!=null && session.isOpen())
                session.close();
        }
    }

    /**
     * Funkcija koja unosi ili menja submission. Funkcija ne vraća ništa, ali baca grešku ukoliko dođe do nekog izuzetka
     * @param submission Submission koji treba uneti u tabelu, ili izmeniti
     */
    public static void updateOrInsert(Submission submission) {
        System.out.println("[Submission] updateOrInsert");
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            submission.LastUpdateTime = new Date();
            session.saveOrUpdate(submission);
            session.getTransaction().commit();
            session.close();
        } catch (PersistenceException err) {
            System.out.println("Error | Constraint violation " + err.getMessage());
            throw err;
        } catch (Exception err) {
            System.err.println("Error | " + err.getMessage() + err.getCause() + err.getLocalizedMessage());
            throw err;
        } finally {
            if (session!=null && session.isOpen())
                session.close();
        }
    }

    /**
     * Funkcija koja unosi ili menja niz submissiona. Koristi se kada stigne odgovor pregledača za više pregledanih radova.
     * @param submissions niz submissiona koja treba uneti ili izmeiti.
     */
    public static void updateOrInsert(List<Submission> submissions) {
        System.out.println("[Submission] updateOrInsertBulk");
        Session session = null;
        String updateSQL = "UPDATE Submissions " +
                "SET Last_Update_Time=:time, " +
                "Message=:message, " +
                "Is_Correct=:isCorrect, " +
                "Waiting_for_response = false, " +
                "no_correct_submissions = no_correct_submissions + :correct_increase " +
                "WHERE User_Id=:userId and Task_Id=:taskId";
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            for(Submission submission : submissions) {
                int rowsUpdated = session.createNativeQuery(updateSQL)
                        .setParameter("time", new Date())
                        .setParameter("message", submission.Message)
                        .setParameter("isCorrect", submission.IsCorrect)
                        .setParameter("correct_increase", (submission.IsCorrect ? 1 : 0))
                        .setParameter("userId", submission.SubmissionId.UserId)
                        .setParameter("taskId", submission.SubmissionId.TaskId)
                        .executeUpdate();

                System.out.println("Updated rows " + rowsUpdated);
            }
            session.getTransaction().commit();
            session.close();
        } catch (PersistenceException err) {
            System.out.println("Error | Constraint violation " + err.getMessage() + err.getLocalizedMessage() + err.getCause());
            throw err;
        } catch (Exception err) {
            System.err.println("Error | " + err.getMessage() + err.getLocalizedMessage());
            throw err;
        } finally {
            if (session!=null && session.isOpen())
                session.close();
        }
    }

    /**
     * Funkcija koja menja objekat submissiona na osnovu dobijenog odgovora pregledača.
     * @param response odgovor je u narednom formatu:
     <pre>
    {
        "requestId": "...", // identifikator zahteva
        "ok": true, // da li je zadatak tačno rešen
        "message": "OK | Execution time 1.782315306\n" // poruka pregledača
    }
    </pre>
     */
    public void graderResponsePayloadToSubmission(String response){
        JSONObject responseJson = new JSONObject(response);
        this.Message = responseJson.getString("message");
        this.IsCorrect = responseJson.getBoolean("ok");
        if(this.IsCorrect)
            this.CorrectSubmissions = this.CorrectSubmissions+1;
        this.WaitingForResponse = false;
    }

    /**
     * Funkcija koja dohvata zadatke koje je student prethodno rešavao.
     * @param userId Id korisnika
     * @return Funkcija vraća niz submissiona sa zadacima koje je student prethodno rešavao.
     */
    public static List<Submission> getSubmissionsForUser(Integer userId){
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();

            List<Submission> ret = new ArrayList<>();

            Query query = session.createQuery("SELECT s.Task.Id, s.WaitingForResponse, s.CorrectSubmissions, s.TotalSubmissions FROM Submission s WHERE SubmissionId.UserId = :userId");
            query.setParameter("userId", userId);
            List<Object[]> submissions = query.list();

            for (Object[] obj : submissions) {
                Submission submission = new Submission();
                Integer taskId = (Integer) obj[0];
                submission.Task = new Task(taskId);
                submission.User = new User(userId);
                submission.WaitingForResponse = Boolean.valueOf((Boolean) obj[1]);
                submission.CorrectSubmissions = (Integer) obj[2];
                submission.TotalSubmissions = (Integer) obj[3];

                ret.add(submission);
            }

            session.close();

            return ret;
        } catch (Error err){
            System.err.println("Error | " + err);
            if (session != null && session.isOpen())
                session.close();
            return new ArrayList<>();
        }
    }
}
