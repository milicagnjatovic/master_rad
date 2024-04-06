package org.example.Conreollers;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import org.example.Tables.User;
import org.json.JSONObject;

import javax.ws.rs.GET;
import javax.ws.rs.core.MediaType;
import java.security.NoSuchAlgorithmException;

@Path("/user")
public class UserController {
    @POST
    @Path("/create")
    @Consumes(MediaType.APPLICATION_JSON)
    public String createUser(String body){

        return createOrUpdateUser(body).toString();
    }

    @POST
    @Path("/update")
    @Consumes(MediaType.APPLICATION_JSON)
    public String updateUser(String body){
        return createOrUpdateUser(body).toString();
    }

    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    public String login(String body){
        try {
            return User.login(new JSONObject(body)).toString();
        } catch (NoSuchAlgorithmException e) {
            return new JSONObject().put("error", e.getMessage()).toString();
        }
    }


    private static JSONObject createOrUpdateUser(String body){
        JSONObject request = new JSONObject(body);
        User user = null;
        try {
            user = new User(request);
            System.out.println(user);
        } catch (NoSuchAlgorithmException e) {
            return new JSONObject().put("Error", e.getMessage());
        }

        // new user missing fields
        if (user.Id == null && (user.Username == null || user.Email == null || user.Password == null)){
            return new JSONObject().put("Error", "Username, email or password missing.");
        }

        String response = "";
        if (user.Id == null)
            response = User.saveUser(user);
        else
            response = User.updateUser(user);

        if (response.isEmpty()){
            return user.toJSON(request.getString("password"));
        }
        return new JSONObject().put("Error", response);
    }
}
