package org.example.Conreollers;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import org.example.RequestHandlers;
import org.example.Tables.Grader;
import org.example.Tables.Submission;
import org.example.Tables.SubmissionID;
import org.example.Tables.Task;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Path("/server")
public class TaskController {
    /**
     * Zahtev za dodavanje zadataka u bazu,
     * @param body telo zahteva treba da bude u narednom formatu:
    <pre>
    {
        "graderId": 1, // id pregledača zaduženog za nove zadatke
        "tasks": [
            {
            "task": "tekst zadatka",
            "solution": "upit koji predstavlja tačno rešenje",
            "ordering": "1" // sortiranje potrebno pregleaču
            }
        ]
    }
    </pre>
     * @return Povratna vrednost funkcije je JSON objekat u narednom formatu:
    <pre>
        {
        "graderResponse": "odgovor pregledača",
        "errors": [] // niz grešaka koje su se desile usput
        }
    </pre>
     */
    @POST
    @Path("/addTasks")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String addTasks(String body){
        System.out.println("[addTasks]");
        System.out.println(body);

        JSONObject request = new JSONObject(body);

        if(!request.has("graderId") || !request.has("tasks")){
            return new JSONObject().put("error", "Error | Request must have following format: {graderId: number, tasks: [...]}").toString();
        }

        Integer graderId = request.getInt("graderId");
        Grader grader = Grader.getById(graderId);
        if (grader == null){
            return new JSONObject().put("error", "Grader does not exist.").toString();
        }

        JSONArray taskArray = request.getJSONArray("tasks");

        JSONObject insertionResponse = Task.insertTasks(taskArray, grader);
        JSONArray errors = insertionResponse.getJSONArray("errors");
        JSONArray tasks = insertionResponse.getJSONArray("tasks");

        String graderResponse;
        try {
            graderResponse = RequestHandlers.sendPostRequest(grader.Endpoint, RequestHandlers.GraderAction.GENERATE, tasks.toString());
        } catch (IOException e) {
            graderResponse = "Error | " + e.getMessage();
        }

        JSONObject returnObject = new JSONObject();
        returnObject.put("errors", errors);
        returnObject.put("graderResponse", new JSONObject(graderResponse));

        return returnObject.toString();
    }

    /**
     *
     * @param body zahtev treba da bude u narednom formatu:
    <pre>
        {
            "userId": "22", // id korisnika
            "taskId": "80", // id zadatka
            "solution": "upit sa rešenjem"
        }
    </pre>
     * @return funckija vraća JSON pbjekat u narednom formatu:
     <pre>
    {
        "requestId": "id poslatog zahteva",
        "ok": false, // da li je rešenje tačno
        "message": "" // poruka pregledača
    }
    Ako dođe do greške odgovor će izgledati ovako:
    {
        "error": "poruka o grešci",
    }
    </pre>
     */
    @POST
    @Path("/checkTask")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String checkTask(String body){
        System.out.println("[check task]");

        try {
            JSONObject request = new JSONObject(body);
            Integer userId = request.getInt("userId");
            Integer taskId = request.getInt("taskId");
            String solution = request.getString("solution");
            SubmissionID submissionID = new SubmissionID(userId, taskId);
            Integer graderId = request.optInt("graderId", 0);

            Submission submission = Submission.getById(submissionID);
            if (submission != null && submission.WaitingForResponse){
                return new JSONObject().put("error", "Already waiting for response " + userId + " " + taskId).toString();
            }

            if (submission == null) {
                submission = new Submission();
                submission.SubmissionId = submissionID;
                if (graderId == 0) {
                    Task task = Task.getById(submissionID.TaskId);
                    if (task == null)
                        return new JSONObject().put("error", "Error | Task does not exist " + submissionID.TaskId).toString();
                    submission.Task = task;
                } else {
                    submission.Task = new Task(submissionID.TaskId);
                    submission.Task.Grader = new Grader(graderId);
                }
            }

            submission.WaitingForResponse = true;
            submission.TotalSubmissions = submission.TotalSubmissions + 1;
            submission.Query = solution;
            submission.Message = null;
            Submission.updateOrInsert(submission);

            JSONObject graderRequest = Submission.prepareGraderRequest(userId, taskId, submission.Query, submission.Task.Ordering);

            if(!Grader.activeGraders.containsKey(submission.Task.Grader.Id)) {
                return new JSONObject().put("error", "Grader is not active").toString();
            }

            Grader grader = Grader.activeGraders.get(submission.Task.Grader.Id);

            String requestEndpoint = grader.Endpoint;

            String response = RequestHandlers.sendPostRequest(requestEndpoint, RequestHandlers.GraderAction.CHECK, graderRequest.toString());

            submission.graderResponsePayloadToSubmission(response);
            Submission.updateOrInsert(submission);

            return response;
        } catch (Exception  e) {
            return new JSONObject("error", e.getMessage()).toString();
        }
    }

    /**
     * Zahtev koji dohvata radove koji čekaju pregledanje i ponovo ih prosleđuje pregledaču.
     * @param body ne koristi se TODO dodati parametar da se bira kom pregledaču se šalje
     * @return Funkcija vraća odgovor pregledača
     */
    @POST
    @Path("/checkTaskBulk")
    @Consumes(MediaType.APPLICATION_JSON)
    public String checkTaskBulk(String body){
        System.out.println("[check task bulk]");
        List<Submission> submissions = Submission.getSubmissionsWaiting();
        Map<Integer, JSONArray> graderRequests = Submission.prepareGraderBulkRequest(submissions);

        JSONArray responses = new JSONArray();
        try {
            for (Integer graderId : graderRequests.keySet()){
                 Grader grader = Grader.activeGraders.get(graderId);
                 JSONArray graderRequestPayload = graderRequests.get(graderId);
                 try {
                     String graderResponse = RequestHandlers.sendPostRequest(grader.Endpoint, RequestHandlers.GraderAction.CHECKBULK, graderRequestPayload.toString());
                     responses.put(graderResponse);

                     JSONArray responsesForBulk = new JSONObject(graderResponse).getJSONArray("results");

//                     going thorough responses and preparing for update
                     List<Submission> submissionsForUpdate = new ArrayList<>();
                     for (Integer i=0; i<responsesForBulk.length(); i++) {
                         JSONObject obj = responsesForBulk.getJSONObject(i);
                         String requestId = obj.getString("requestId");
                         Integer taskId = Integer.valueOf(requestId.split("-")[0]);
                         Integer userId = Integer.valueOf(requestId.split("-")[1]);
                         Boolean isCorrect = obj.getBoolean("ok");
                         String message = obj.getString("message");

                         submissionsForUpdate.add(new Submission(userId, taskId, isCorrect, message));
                     }
//                     update submissions
                     Submission.updateOrInsert(submissionsForUpdate);

                     System.out.println("GraderResponse " + graderId + " : " + graderResponse);

                 } catch (Exception e) {
                     System.out.println("Error | " + graderId + " " + e.getMessage());
                     responses.put(e.getMessage());
                 }
            }
            return responses.toString();
        } catch (Exception e) {
            return new JSONObject().put("error", e.getMessage()).toString();
        }
    }

    /**
     * Zahtev kojim se menja zadatak, na primer tekst zadatka, rešenje ili sortiranje.
     * @param body  Telo zahteva treba da bude u narednom formatu:
     <pre>
    {
        "graderId": 1, // id pregledača
        "tasks": [
            {
            "taskId": 4, // id zadtaka
            "task": "Tekst zadatka",
            "solution": "select * from da.dosije",
            "ordering": "2"
            }
        ]
    }
    U objektima niza tasks je jedini obavezan element taskId, ostali elementi su opcioni i treba ih navesti ukoliko se vrednost menja.
    Nakon unosa vrednosti se šalje zahtev pregledaču da generiše fajl sa rešenjem koji će kasnije biti potreban za pregledanje radova.
    Ukoliko je potrebno samo regenerisati rešenje na pregledaču objketi niza tasks treba da sadrže samo taskId.
    </pre>
     * @return
     */
    @POST
    @Path("/updateTasks")
    @Consumes(MediaType.APPLICATION_JSON)
    public String updateTask(String body){
        System.out.println("[updateTask]");

        JSONObject request = new JSONObject(body);

        if(!request.has("graderId") || !request.has("tasks")){
            JSONObject ret = new JSONObject();
            ret.put("message", "Error | Request must have following format: {graderId, tasks: [{taskId, solution, ordering}, {}, ...]");
            return ret.toString();
        }

        Integer graderId = request.getInt("graderId");
        Grader grader = Grader.getById(graderId);
        if (grader == null){
            return new JSONObject().put("error", "Error | Grader does not exists").toString();
        }

        JSONArray taskArray = request.getJSONArray("tasks");

        JSONObject updateTasksResponse = Task.updateTasks(taskArray, grader);
        JSONArray errors = updateTasksResponse.getJSONArray("errors");
        JSONArray tasks = updateTasksResponse.getJSONArray("tasks");

        System.out.println("[Tasks] " + tasks);

        String graderResponse;
        try {
            graderResponse = RequestHandlers.sendPostRequest(grader.Endpoint, RequestHandlers.GraderAction.GENERATE, tasks.toString());
        } catch (IOException e) {
            graderResponse = "Error | " + e.getMessage();
        }

        JSONObject returnObject = new JSONObject();
        returnObject.put("errors", errors);
        returnObject.put("graderResponse", graderResponse);

        return returnObject.toString();
    }
}
