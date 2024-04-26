package org.example.Conreollers;

import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;

@Path("/message")
public class MessageController {
    @POST
    @Path("/askQuestion")
    public static String askQuestion(String body){
        return "ok";
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
