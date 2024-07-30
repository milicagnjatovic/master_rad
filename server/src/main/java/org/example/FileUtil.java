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
    /**
     * Naziv fajla u kom se čuvaju zadaci koji se šalju korisnicima.
     */
    public static final String FILE_WITH_TASKS = "file_with_tasks.json";

    /**
     * Naziv fajla u kom se čuvaju rang liste sa uspesima studentata i uspehu po zadacima.
     */
    public static final String FILE_WITH_STATS = "file_with_stats.json";


    /**
     * Naziv fajla u kom se čuvaju obavestenja.
     */
    public static final String FILE_WITH_NOTIFICATIONS = "file_with_notifications.json";

    /**
     *
     * @param fileName - naziv fajla u koji treba unesti podatke.
     * @param data - tekst koji treba uneti u fajl.
     */
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

    /**
     *
     * @param fileName - naziv fajla iz koga treba pročitati tekst.
     * @return Funkcija vraća tekst iz fajla.
     */
    public static String readFromFile(String fileName){
        try {
            File f = new File(fileName);
            if (!f.exists()){
                return "404";
            }

            List<String> tasksFromFile = Files.readAllLines(java.nio.file.Path.of(fileName));
            return String.join("", tasksFromFile);
        } catch (IOException e) {
            System.out.println("Error | " + e.getMessage());
            return new JSONObject().put("error", e.getMessage()).toString();
        }

    }

    public static void removeFile(String fileName){
        File file = new File(fileName);
        if (file.exists()) {
            file.delete();
            System.out.println("Removed file " + fileName);
        }
    }
}
