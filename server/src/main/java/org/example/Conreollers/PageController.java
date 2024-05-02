package org.example.Conreollers;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import org.example.FileUtil;
import org.example.Tables.Roles;
import org.example.Tables.Submission;
import org.example.Tables.Task;
import org.example.Views.TaskStatistic;
import org.example.Views.UserStatistic;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.Map;

/**
 * Klasa za kontrolere neophodne za početnu stranicu, poput dohvatanja zadataka i rang listi.
 */
@Path("/page")
public class PageController {

    /**
     * Dohvata zadatke iz fajla sa zadacim.
     * @return Vraća JSONArray string sa zadacima.
     */
    @POST
    @Path("/getAllTasks")
    @Produces(MediaType.APPLICATION_JSON)
    public String getAllTasks(String body){
        System.out.println("[getAllTasks]");
        Integer roleID = new JSONObject(body).optInt("roleId", -1);
        if (roleID == -1){
            return new JSONObject().put("error", "Missing roleId").toString();
        }

        String fileContent = Task.getTasksForRole(roleID).toString();
        if (new JSONArray(fileContent).length() == 0){
            return new JSONObject().put("error", "There are no tasks for given role.").toString();
        }

        return fileContent;
    }

    /**
     * Identično /getAllTaska, razlika je što ova funkcija ponovo dohvata zadatke iz baze i unosi ih u fajl.
     * @return Vraća JSONArray string sa zadacima.
     */
    @GET
    @Path("/refreshTasks")
    public static String refreshTasks(){
        System.out.println("[refreshAllTasks]");

        List<Roles> roles = Roles.getAllRoles();
        for (Roles role : roles){
            FileUtil.removeFile(role.Id + FileUtil.FILE_WITH_TASKS);
        }

        Map<Integer, List<Task>> tasksPerRole = Task.getAllTasksPerRole();
        for(Integer roleId : tasksPerRole.keySet()) {

            JSONArray tasksJSON = Task.tasksToJSONArray(tasksPerRole.get(roleId));
            FileUtil.writeToFile(roleId + FileUtil.FILE_WITH_TASKS, tasksJSON.toString());
        }

        return new JSONObject().put("message", "generated ").toString();
    }

    /**
     * Zahtev za prethodno rešene zadatke korisnika.
     * @param body Telo zahteva treba da bude u narednom formatu:
    <pre>
    {
    "userId": 1 // id korisnika
    }
    </pre>
     * @return Funkcija vraća JSONArray zadataka koje je korisnik prethodno rešavao, rezultat je u narednom formatu:
     <pre>
    [
        {
            "isWaitingForResponse": false, // da li se čeka odgovor pregledača za ovaj zadatak
            "noTotalSubmissions": 1, // broj ukupno rešenja koje je student poslao
            "noCorrect": 1, // broj tačnih rešenja koje je student poslao
            "taskId": 74 // id zadatka koji je rešavan
        }
    ]
    </pre>
     */
    @POST
    @Path("/getTasksForUser")
    public String getTasksForUser(String body){
        System.out.println("[getTasksForUser]");

        JSONObject request = new JSONObject(body);
        Integer userId = request.optInt("userId", -1);

        List<Submission> submissions = Submission.getSubmissionsForUser(userId);

        JSONArray ret = new JSONArray();

        for (Submission submission: submissions) {
            ret.put(submission.toJSON());
        }

        return ret.toString();
    }

    /**
     *
     * @return Funkcija vraća rang listu korisnika i rang listu najuspešnije rešenih zadataka koji se nelaze u fajlu.
     */
    @GET
    @Path("/getStats")
    public String getTaskStatistics(){
        System.out.println("[getStats]");
        return FileUtil.readFromFile(FileUtil.FILE_WITH_STATS);
    }

    /**
     *
     * @return Funkcija vraća rang listu korisnika i rang listu najuspešnije rešenih zadataka koji se nelaze u fajlu, nakon što osvežava fajl podacima iz baze.
     */
    @GET
    @Path("/refreshStats")
    public String refreshStats(){
        System.out.println("[refreshStats]");
        List<TaskStatistic> taskStats = TaskStatistic.getAllStats();
        List<UserStatistic> userStats = UserStatistic.getAllStats();
        JSONArray taskJSON = TaskStatistic.getJSONfromList(taskStats);
        JSONArray userJSON = UserStatistic.getJSONfromList(userStats);

        JSONObject obj = new JSONObject();
        obj.put("taskStats", taskJSON);
        obj.put("userStats", userJSON);

        FileUtil.writeToFile(FileUtil.FILE_WITH_STATS, obj.toString());

        return obj.toString();
    }
}
