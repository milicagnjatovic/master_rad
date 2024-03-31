package org.example.Tables;

import org.example.HibernateUtil;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.query.Query;
import org.json.JSONObject;

import javax.persistence.*;
import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "USERS")
public class User {
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer Id;

    @Column(name = "USERNAME", unique = true, nullable = false)
    public String Username;

    @Column(name = "PASSWORD", nullable = false)
    public String Password;

    @Column(name = "EMAIL", unique = true, nullable = false)
    public String Email;

    @Column(name = "FIRST_NAME")
    public String FirstName;

    @Column(name = "LAST_NAME")
    public String LastName;

    @Column(name = "CREATED_DATE")
    public Date CreatedDate;

    @Column(name = "CORRECT_SUBMISSIONS")
    public Integer CorrectSubmissions;

    @Column(name = "INCORRECT_SUBMISSIONS")
    public Integer IncorrectSubmissions;

    @ManyToOne()
    @JoinColumn(name = "ROLE_ID", referencedColumnName = "ID")
    public Roles Role;

    public User(Integer id){
        this.Id = id;
    }

    public User(){}

    public User(JSONObject obj) throws NoSuchAlgorithmException {
        if (obj.has("id")){
            this.Id = obj.getInt("id");
        }

        this.Username = obj.optString("username", null);
        this.Email = obj.optString("email", null);
        this.FirstName = obj.optString("firstname", "");
        this.LastName = obj.optString("lastname", "");
        this.Role = new Roles();

        if(obj.has("role"))
            this.Role.Id = obj.getInt("role");

        String password = obj.optString("password", null);
        if (password != null)
            this.Password = encodePassword(password);
        this.CreatedDate = new Date();
    }

    public static String saveUser(User user){
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();

            if (user.Id == null) {
                Query query = session.createQuery("SELECT Id FROM User WHERE Email = :email OR Username = :username");
                query.setParameter("email", user.Email);
                query.setParameter("username", user.Username);
                List<Object[]> users = query.list();
                if (users.size() > 0){
                    return "Username or email are taken";
                }
            }

            Roles role = session.load(Roles.class, user.Role.Id);
            System.out.println(role);
            user.Role = role;

            session.saveOrUpdate(user);
            session.getTransaction().commit();
            session.close();
            return "";
        } catch (ConstraintViolationException e){
            return "Error | Constraint " + e.getMessage();
        }
        catch (HibernateException e) {
            if (session.getTransaction().isActive()){
                session.getTransaction().rollback();
            }
            return "Error | " + e.getMessage();
        } finally {
            if (session!=null && session.isOpen())
                session.close();
        }
    }
    private static String encodePassword(String password) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(password.getBytes());
        byte[] hashed = md.digest();

        String hashedPassword = DatatypeConverter.printHexBinary(hashed);

        System.out.println(hashedPassword);

        return hashedPassword;
    }

    @Override
    public String toString() {
        return this.Username + " " + this.Id + " " + this.FirstName + " " + this.LastName + " " + this.Role.Id;
    }
}
