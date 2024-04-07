package org.example.Conreollers;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import org.example.Tables.Task;
import org.json.JSONArray;

import javax.ws.rs.core.MediaType;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

@Path("/page")
public class PageController {
    private static final String FILE_WITH_TASKS = "file_with_tasks.json";

    @GET
    @Path("/getAllTasks")
    @Produces(MediaType.APPLICATION_JSON)
    public String getAllTasks(){
        System.out.println("[getAllTasks]");
        try {
            List<String> tasksFromFile = Files.readAllLines(java.nio.file.Path.of(PageController.FILE_WITH_TASKS));
            return String.join("", tasksFromFile);
        } catch (IOException e) {
            System.err.println("Error | " + e.getMessage());
            return "Error retrieving tasks";
        }
    }

    @GET
    @Path("/refreshTasks")
    public String refreshTasks(){
        System.out.println("[refreshAllTasks]");

        List<Task> tasks = Task.getAllTasks();
        for (Task task: tasks)
            System.out.println(task);

        JSONArray tasksJSON = Task.tasksToJSONArray(tasks);

        File fileWithTasks = new File(PageController.FILE_WITH_TASKS);
        try {
            if (fileWithTasks.createNewFile()){
                System.out.println("File created");
            } else {
                System.out.println("File already exists");
            }

            FileWriter writer = new FileWriter(fileWithTasks.getName());
            writer.write(tasksJSON.toString());
            writer.close();

        } catch (IOException e) {
            System.err.println("Error | " + e.getMessage());
        }

        return tasksJSON.toString();
    }
}
