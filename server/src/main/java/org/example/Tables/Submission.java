package org.example.Tables;

import org.example.HibernateUtil;
import org.hibernate.Session;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "SUBMISSIONS")
public class Submission {
    @EmbeddedId
    public SubmissionID SubmissionId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "TASK_ID", referencedColumnName = "ID", insertable = false, updatable = false)
    public org.example.Tables.Task Task;

    @ManyToOne
    @JoinColumn(name = "USER_ID", referencedColumnName = "ID", insertable = false, updatable = false)
    public Users User;

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
        }
    }

    public static void updateOrInsert(Submission submission) throws Error {
        System.out.println("[Submission] updateOrInsert");
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            session.saveOrUpdate(submission);
            session.getTransaction().commit();
            session.close();
        } catch (PersistenceException err) {
            System.out.println("Error | Constraint violation");
            throw err;
        } catch (Exception err) {
            System.err.println("Error | " + err.getMessage());
            throw err;
        }
    }
}
