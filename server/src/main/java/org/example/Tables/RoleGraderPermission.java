package org.example.Tables;

import org.example.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.json.JSONObject;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Table taking care of which user has access to which tasks.
 * Permission are assigned by graders.
 * If user has access to grader he has access to all the tasks for that grader.
 */
@Entity
@Table(name = "PERMISSION")
public class RoleGraderPermission {
    @EmbeddedId
    public RoleGraderPermissionID PermissionId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ROLE_ID", referencedColumnName = "ID", insertable = false, updatable = false)
    public Role PermissionRole;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "GRADER_ID", referencedColumnName = "ID", insertable = false, updatable = false)
    public Grader PermissionGrader;

    public RoleGraderPermission(){}

    public RoleGraderPermission(Integer roleId, Integer graderId){
        this.PermissionId = new RoleGraderPermissionID(roleId, graderId);
    }

    public RoleGraderPermission(RoleGraderPermissionID id){
        this.PermissionId = id;
    }

    @Override
    public String toString() {
        return "Role " + this.PermissionId.RoleId + " Grader " + this.PermissionId.GraderId;
    }

    public JSONObject toJSON(){
        JSONObject obj = new JSONObject();
        obj.put("grader", this.PermissionGrader.Name);
        obj.put("graderId", this.PermissionGrader.Id);
        obj.put("role", this.PermissionRole.Name);
        obj.put("roleId", this.PermissionRole.Id);
        return obj;
    }

    public static List<RoleGraderPermission> getAllPermissions(){
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            Query query = session.createQuery("FROM RoleGraderPermission ", RoleGraderPermission.class);
            List<RoleGraderPermission> ret = query.list();
            session.close();
            return ret;
        } catch (Error err) {
            System.out.println("Error | " + err.getMessage());
            if (session != null && session.isOpen())
                session.close();
        }
        return new ArrayList<>();
    }

    public static String insertPermissions(List<RoleGraderPermission> permissions){
        System.out.println("inser " + permissions);
        Session session = null;
        String error = "";
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();

            for (RoleGraderPermission permission : permissions){
                session.saveOrUpdate(permission);
            }

            try {
                session.getTransaction().commit();
            } catch (PersistenceException exception) {
                error = exception.getMessage();
            }

            session.close();
            return error;
        } catch (Error err) {
            System.out.println("Error | " + err.getMessage());
            if (session != null && session.isOpen()) {
                if (session.getTransaction().isActive())
                    session.getTransaction().rollback();
                session.close();
            }
            return err.getMessage();
        }
    }

    public static String deletePermissions(List<RoleGraderPermission> permissions){
        Session session = null;
        String error = "";
        try {
            session = HibernateUtil.getSessionFactory().openSession();

            session.beginTransaction();

            for(RoleGraderPermission permission : permissions) {
                session.delete(permission);
            }

            try {
                session.getTransaction().commit();
            } catch (PersistenceException exception) {
                error = exception.getMessage();
            }
            session.close();
        } catch (Error err) {
            System.out.println("Error | " + err.getMessage());
            if (session != null && session.isOpen()) {
                if (session.getTransaction().isActive())
                    session.getTransaction().rollback();
                session.close();
            }
        }
        return error;
    }
}
