package org.example.Tables;

import org.example.HibernateUtil;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.annotations.DynamicUpdate;
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
@DynamicUpdate
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
        this.FirstName = obj.optString("firstname", null);
        this.LastName = obj.optString("lastname", null);
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

            Query query = session.createQuery("SELECT Id FROM User WHERE Email = :email OR Username = :username");
            query.setParameter("email", user.Email);
            query.setParameter("username", user.Username);
            List<Object[]> users = query.list();
            if (users.size() > 0){
                return "Username or email are taken";
            }

            Roles role = session.load(Roles.class, user.Role.Id);
            System.out.println(role);
            user.Role = role;

            session.save(user);
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

    public static String updateUser(User user){
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();

            Query query = session.createQuery("SELECT Id FROM User WHERE (Email = :email OR Username = :username) AND Id != :id");
            query.setParameter("email", user.Email);
            query.setParameter("username", user.Username);
            query.setParameter("id", user.Id);
            List<Object[]> users = query.list();
            if (users.size() > 0){
                return "Username or email are taken";
            }

            User existingUser = session.get(User.class, user.Id);
            if (user.FirstName!=null) existingUser.FirstName = user.FirstName;
            if (user.LastName!=null) existingUser.LastName = user.LastName;
            if (user.Username!=null) existingUser.Username = user.Username;
            if (user.Email!=null) existingUser.Email = user.Email;
            if (user.Password!=null) existingUser.Password = user.Password;
            if (user.Role!=null) existingUser.Role = session.load(Roles.class, user.Role.Id);

            session.update(existingUser);

            user.FirstName = existingUser.FirstName;
            user.LastName = existingUser.LastName;
            user.Username = existingUser.Username;
            user.Email = existingUser.Email;

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


    public static JSONObject login(JSONObject body) throws NoSuchAlgorithmException {
        if(!body.has("username") || !body.has("password"))
            return new JSONObject().put("error", "Username or password missing.");

        String username = body.getString("username");
        String password = body.getString("password");

        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();

            Query query = session.createQuery("FROM User WHERE Username = :username", User.class);
            query.setParameter("username", username);
            List<User> users = query.list();
            if (users.size() == 0) {
                return new JSONObject().put("error", "User does not exists.");
            }
            User user = users.get(0);

            String encodedPassword = encodePassword(password);
            if (!encodedPassword.equals(user.Password)){
                return new JSONObject().put("error", "Wrong password.");
            }

            return user.toJSON(password);
        } catch (HibernateException e) {
            if (session.getTransaction().isActive()){
                session.getTransaction().rollback();
            }
            return new JSONObject("error", e.getMessage());
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

    public JSONObject toJSON(String password){
        JSONObject ret = new JSONObject();
        ret.put("username", this.Username);
        ret.put("password", password);
        ret.put("firstname", this.FirstName);
        ret.put("lastname", this.LastName);
        ret.put("id", this.Id);

        return ret;
    }
}
