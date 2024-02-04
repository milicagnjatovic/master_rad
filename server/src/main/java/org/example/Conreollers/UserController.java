package org.example.Conreollers;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import org.example.Tables.Grader;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.ws.rs.GET;
import javax.ws.rs.core.MediaType;

@Path("/user")
public class UserController {


    @POST
    @Path("/create")
    @Consumes(MediaType.APPLICATION_JSON)
    public String createUser(String body){
        return "user created";
    }

    @POST
    @Path("/update")
    @Consumes(MediaType.APPLICATION_JSON)
    public String updateUser(String body){
        return "user updated";
    }
}
