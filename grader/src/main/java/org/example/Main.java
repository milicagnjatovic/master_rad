package org.example;

import org.hibernate.Session;
import org.hibernate.query.Query;

import org.example.Task;
import org.json.JSONObject;

import java.util.Date;
import java.util.List;
import java.util.Objects;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {
    public static void main(String[] args) {
//        TaskHandler.generateSolutions();

        JSONObject obj = new JSONObject();

        obj.put("taskId", 22);
        obj.put("solution", "select * from da.predmet");
        obj.put("user", "mi123");
        System.out.println(obj.toString());
        TaskHandler.checkTask(obj.toString());
//        Session session = null;
//        try {
//            session = HibernateUtil.getSessionFactory().openSession();
//            Query query = session.createQuery("SELECT Task from Task");
//            List<String> res = query.list().stream().toList();
//
//            Query<Object[]> query = session.createQuery("SELECT Id, Solution, Task from Task");
//            List<Object[]> res = query.list();
//            System.out.println(res);
//            for(Object[] t : res){
//                System.out.println(t[0].toString() + ' ' + t[1] + ' ' + t[2]);
//            }
//
//        } catch (Error e){
//            System.err.println(e);
//        } finally {
//            session.close();
//        }

        System.out.println("DONE2");
    }
}