package org.example;

import org.hibernate.Session;
import org.hibernate.query.Query;

import org.example.Task;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;
import java.util.List;
import java.util.Objects;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {
    public static void main(String[] args) {
        TaskHandler.generateSolutions();

//        JSONObject obj = new JSONObject();
//        obj.put("taskId", 22);
//        obj.put("solution", "select * from da.predmet");
//        obj.put("user", "mi123");
//        System.out.println(obj.toString());
//        TaskHandler.checkTask(obj.toString());

//        try {
//            String text = new String(Files.readAllBytes(Path.of("zadaci.json")));
////            System.out.println(text);
//            JSONArray arr = new JSONObject(text).getJSONArray("zadaci");
//            TaskHandler.insertNewTasks(arr);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
    }
}