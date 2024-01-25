package org.example;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;

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
