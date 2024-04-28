package org.example.Tables;

import jakarta.validation.ConstraintViolationException;
import org.example.HibernateUtil;
import org.hibernate.Session;
import org.json.JSONObject;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "MESSAGES")
public class Messages {
    @EmbeddedId
    public MessageID Id;

    @Column(name = "QUESTION")
    public String Question;

    @Column(name="PROFESSOR_ID", nullable = false)
    public Integer ProfessorId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PROFESSOR_ID", referencedColumnName = "ID", insertable = false, updatable = false)
    public User Professor;

    @Column(name = "RESPONSE")
    public String Response;

    @Column(name = "CREATED_DATE")
    public Date CreatedDate = new Date();

//    OneToOne u odredjenim slucajevima daje gresku da je prosledjen SubmissionId umesto MessageId
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "TASK_ID", referencedColumnName = "TASK_ID", insertable = false, updatable = false),
            @JoinColumn(name = "STUDENT_ID", referencedColumnName = "USER_ID", insertable = false, updatable = false)
    })
    public Submission SubmissionInQuestion;

    public static String insertMessage(Messages message){
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();

            Messages existingMessage = session.get(Messages.class, message.Id);
            if (existingMessage != null){
                session.close();
                return "Question is already asked";
            }
            System.out.println(existingMessage);

            Submission existingSubmissions = session.get(Submission.class, message.SubmissionInQuestion.SubmissionId);
            if (existingSubmissions == null){
                return "Try solving task first";
            }
            System.out.println(existingSubmissions);

            session.beginTransaction();
            message.SubmissionInQuestion = session.get(Submission.class, message.SubmissionInQuestion.SubmissionId);
            message.Professor = session.get(User.class, message.Professor.Id);

            System.out.println(message);

            session.save(message);
            session.getTransaction().commit();

            session.close();
            return "";
        } catch (ConstraintViolationException e){
            System.err.println("Error | " + e.getMessage());
            if (session!=null && session.isOpen()){
                if (session.getTransaction().isActive())
                    session.getTransaction().rollback();
                session.close();
            }
            return "Constraint violation";
        }
        catch (Exception e){
            System.err.println("Error | " + e.getMessage());
            if (session!=null && session.isOpen()){
                if (session.getTransaction().isActive())
                    session.getTransaction().rollback();
                session.close();
            }
            return e.getMessage();
        }
    }

    public static String responseToMessage(Messages message){
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();

            Messages existingMessage = session.get(Messages.class, message.Id);
            if (existingMessage == null){
                session.close();
                return "Question does not exist";
            }

            session.beginTransaction();

            existingMessage.Response = message.Response;
            session.saveOrUpdate(existingMessage);
            session.getTransaction().commit();
            session.close();
            return "";
        } catch (ConstraintViolationException e){
            System.err.println("Error | " + e.getMessage());
            if (session!=null && session.isOpen()){
                if (session.getTransaction().isActive())
                    session.getTransaction().rollback();
                session.close();
            }
            return "Constraint violation";
        }
        catch (Exception e){
            System.err.println("Error | " + e.getMessage());
            if (session!=null && session.isOpen()){
                if (session.getTransaction().isActive())
                    session.getTransaction().rollback();
                session.close();
            }
            return e.getMessage();
        }
    }

    public Messages(JSONObject obj){
        System.out.println(obj);
        Integer userId = obj.optInt("userId", -1);
        Integer taskId = obj.optInt("taskId", -1);
        Integer professorId = obj.optInt("professorId", -1);
        if (userId==-1 || taskId==-1 || professorId==-1)
            return;
        this.Id = new MessageID(userId, taskId);
        this.Question = obj.optString("question", null);
        this.Response = obj.optString("response", null);
        this.ProfessorId = professorId;
        this.Professor = new User(professorId);
        SubmissionID submissionID = new SubmissionID(userId, taskId);
        System.out.println("is mesasage " + (submissionID instanceof SubmissionID));
        System.out.println("is messafe " + (this.Id instanceof MessageID));
        this.SubmissionInQuestion = new Submission(submissionID);
    }

    public Messages(){}

    @Override
    public String toString() {
        return this.Id.TaskId + " " + this.Id.StudentId + " " + this.Professor.Id + " " + this.Question;
    }
}
