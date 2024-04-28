package org.example.Conreollers;

import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import org.example.Tables.Messages;
import org.json.JSONObject;

@Path("/message")
public class MessageController {
    @POST
    @Path("/askQuestion")
    public static String askQuestion(String body){
        JSONObject req = new JSONObject(body);

        if (!req.has("userId") || !req.has("professorId") || !req.has("question") || !req.has("taskId"))
            return new JSONObject().put("error", "Missing userId, professorsId, taskId or question").toString();

        Messages message = new Messages(req);
        if (message == null)
            return new JSONObject().put("error", "Cannot create object.").toString();
        String error = Messages.insertMessage(message);

        return new JSONObject().put("error", error).toString();
    }

    @POST
    @Path("/respondToQuestion")
    public static String respondToQuestion(String body){
        return "ok";
    }

    @POST()
    @Path("/getAllQuestionsForUser")
    public static String getAllQuestions(){
        return "ok";
    }
}
