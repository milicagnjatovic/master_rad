package org.example.Conreollers;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import org.example.Tables.Messages;
import org.example.Tables.User;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

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
        System.out.println("[respondToQuestion]");
        JSONObject req = new JSONObject(body);
        if (!req.has("userId") || !req.has("taskId") || !req.has("response") || !req.has("professorId")){
            return new JSONObject().put("error", "Missing userId, taskId, professorId or response").toString();
        }

        Messages messages = new Messages(req);
        String updateMessage = Messages.responseToMessage(messages);
        return new JSONObject().put("error", updateMessage).toString();
    }

    @POST()
    @Path("/getAllQuestionsForUser")
    public static String getAllQuestions(String body){
        System.out.println("[getAllQuestionsForUser]");
        JSONObject req = new JSONObject(body);

        Integer userId = req.optInt("userId", -1);
        if (userId == -1){
            return new JSONObject().put("error", "Missing userId").toString();
        }

        List<Messages> messages = Messages.getAllMessages(userId);

        JSONArray ret = new JSONArray();
        for(Messages message : messages){
            ret.put(message.toJSON());
        }
        return ret.toString();
    }

    @GET
    @Path("/availableProfessors")
    public static String getAvailableProfessors(){
        System.out.println("[availableProfessors]");
        List<User> users = User.usersAvailableToAnswerQuestions();
        JSONArray arr = new JSONArray();
        for (User user : users){
            JSONObject obj = new JSONObject();
            obj.put("username", user.Username);
            obj.put("id", user.Id);
            arr.put(obj);
        }
        return arr.toString();
    }
}
