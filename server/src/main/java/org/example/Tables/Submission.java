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

    @Column(name = "QUERY")
    public String Query;

    @Column(name = "IS_CORRECT")
    public Boolean IsCorrect = false;

    @Column(name = "MESSAGE")
    public String Message;

    @Column(name = "LAST_UPDATE_TIME")
    public Date LastUpdateTime = new Date();

    @Column(name = "WAITING_FOR_RESPONSE")
    public boolean WaitingForResponse = false;

    @Column(name = "NO_CORRECT_SUBMISSIONS")
    public Integer CorrectSubmissions = 0;

    @Column(name = "NO_SUBMISSIONS")
    public Integer TotalSubmissions = 0;

    @Override
    public String toString() {
        return Task.Id + " " + User.Id + " " + this.Query;
    }

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

    public static JSONObject prepareGraderRequest(Integer userId, Integer taskId, String query, String ordering){
        JSONObject graderRequest = new JSONObject();
        graderRequest.put("requestId", taskId + "-" + userId);
        graderRequest.put("taskId", taskId);
        graderRequest.put("solution", query);
        if (ordering != null && !ordering.isEmpty())
            graderRequest.put("ordering", ordering);
        System.out.println("Prepared " + graderRequest.toString());
        return graderRequest;
    }

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
//        this.User = new User(userId);
//        this.Task = new Task(taskId);
        this.SubmissionId = new SubmissionID(userId, taskId);
        this.Message = message;
        this.IsCorrect = isCorrect;
    }

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

    public static void updateOrInsert(Submission submission) throws Exception {
        System.out.println("[Submission] updateOrInsert");
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            submission.LastUpdateTime = new Date();
            submission.WaitingForResponse = false;
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

    public static void updateOrInsert(List<Submission> submissions) throws Exception {
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
//                        .setParameter("id", submission.SubmissionId)
                        .setParameter("userId", submission.SubmissionId.UserId)
                        .setParameter("taskId", submission.SubmissionId.TaskId)
                        .executeUpdate();

                System.out.println("Updated rows " + rowsUpdated);
//                submission.LastUpdateTime = new Date();
//                session.saveOrUpdate(submission);
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

    public void graderResponsePayloadToSubmission(String response){
        JSONObject responseJson = new JSONObject(response);
        this.Message = responseJson.getString("message");
        this.IsCorrect = responseJson.getBoolean("ok");
        if(this.IsCorrect)
            this.CorrectSubmissions = this.CorrectSubmissions+1;
        this.WaitingForResponse = false;
    }
}
