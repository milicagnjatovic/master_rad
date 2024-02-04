package org.example.Conreollers;

import jakarta.ws.rs.*;
import org.example.Tables.Grader;
import org.json.JSONObject;

import javax.ws.rs.core.MediaType;

@Path("/grader")
public class GradersController {
    @GET
    @Path("/getGraders")
    @Produces(MediaType.APPLICATION_JSON)
    public String getGraders(){
        System.out.println("[getGraders]");
        return Grader.gradersString;
    }

    @GET
    @Path("/getAllGraders")
    @Produces(MediaType.APPLICATION_JSON)
    public String getAllGraders(){
        System.out.println("[getAllGraders]");
        return Grader.getAllGraders();
    }

    @GET
    @Path("/refreshGraders")
    @Produces(MediaType.APPLICATION_JSON)
    public String refreshGraders(){
        System.out.println("[refreshGraders]");
        Grader.retrieveGradersToMap();
        System.out.println(Grader.gradersString);
        return Grader.gradersString;
    }

    @POST
    @Path("/addGrader")
    @Consumes(MediaType.APPLICATION_JSON)
    public String addGrader(String body){
        System.out.println("[addGrader]");

        JSONObject request = new JSONObject(body);

        if(!request.has("name") || !request.has("endpoint") || !request.has("active")) {
            return "Error | missing name, endpoint or active";
        }

        Grader grader = new Grader();
        grader.Name = request.getString("name");
        grader.Endpoint = request.getString("endpoint");
        grader.Active = request.getBoolean("active");
        return Grader.insertOrUpdateGrader(grader);
    }

    @POST
    @Path("/updateGrader")
    @Consumes(MediaType.APPLICATION_JSON)
    public String updateGrader(String body){
        System.out.println("[updateGrader]");
        JSONObject request = new JSONObject(body);
        Integer id = request.getInt("id");
        Grader grader = Grader.getById(id);
        if (grader == null){
            return "Error | Grader does not exist";
        }

        System.out.println(grader);
        String tmp = request.optString("endpoint", "");
        System.out.println("tmp" + tmp);
        if(!tmp.isBlank())
            grader.Endpoint = tmp;

        tmp = request.optString("name", "");
        System.out.println("tmp" + tmp);
        if(!tmp.isBlank())
            grader.Name = tmp;

        System.out.println("tmp " + request.has("active"));
        if(request.has("active"))
            grader.Active=request.getBoolean("active");
        System.out.println("ready for update");
        return Grader.insertOrUpdateGrader(grader);
    }
}
