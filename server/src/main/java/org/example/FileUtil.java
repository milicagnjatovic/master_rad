package org.example;

import org.example.Conreollers.PageController;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class FileUtil {
    public static final String FILE_WITH_TASKS = "file_with_tasks.json";
    public static final String FILE_WITH_STATS = "file_with_stats.json";

    public static void writeToFile(String fileName, String data) {
        File fileWithTasks = new File(fileName);
        try {
            if (fileWithTasks.createNewFile()) {
                System.out.println("File created");
            } else {
                System.out.println("File already exists");
            }

            FileWriter writer = new FileWriter(fileWithTasks.getName());
            writer.write(data);
            writer.close();
        } catch (IOException e) {
            System.out.println("Error | " + e.getMessage());
        }
    }

    public static String readFromFile(String fileName){
        try {
            List<String> tasksFromFile = Files.readAllLines(java.nio.file.Path.of(fileName));
            return String.join("", tasksFromFile);
        } catch (IOException e) {
            System.out.println("Error | " + e.getMessage());
            return new JSONObject().put("error", e.getMessage()).toString();
        }

    }
}
