package org.example.Tables;

import org.example.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.query.Query;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "ROLES")
public class Roles {
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer Id;

    @Column(name = "NAME")
    public String Name;

    @Column(name = "DESCRIPTION")
    public String Description;


    @Column(name = "CREATED_DATE")
    public Date CreatedDate;

    public Roles() {
        this.Id = 4;
    }

    @Override
    public String toString() {
        return this.Name + " " + this.Id;
    }

    public static List<Roles> getAllRoles(){
        Session session = null;
        List<Roles> ret = new ArrayList<>();
        try {
            session = HibernateUtil.getSessionFactory().openSession();

            Query query = session.createQuery("FROM Roles", Roles.class);
            ret.addAll(query.list());
            session.close();
            return ret;
        } catch (Error err){
            if(session != null && session.isOpen())
                session.close();
            System.out.println("Error | " + err.getMessage());
            return ret;
        }
    }
}
