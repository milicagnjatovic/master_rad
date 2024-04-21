package org.example.Conreollers;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import org.example.Tables.RoleGraderPermission;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/roleGraderPermission")
public class RoleGraderPermissionController {
    @POST
    @Path("/addPermission")
    @Produces(MediaType.APPLICATION_JSON)
    public String addPermission(String body){
        System.out.println("[addPermission]");
        JSONObject request = new JSONObject(body);

        Integer roleID = new JSONObject(body).optInt("roleId", -1);
        Integer graderID = new JSONObject(body).optInt("graderId", -1);

        if(roleID == -1 || graderID == -1){
            return new JSONObject("error", "Missing roleId or graderId").toString();
        }

        RoleGraderPermission permission = RoleGraderPermission.insertPermission(roleID, graderID);
        System.out.println(permission);

        return "ok";
    }
    @POST
    @Path("/removePermission")
    @Produces(MediaType.APPLICATION_JSON)
    public String removePermission(String body){
        System.out.println("[addPermission]");
        JSONObject request = new JSONObject(body);

        Integer roleID = new JSONObject(body).optInt("roleId", -1);
        Integer graderID = new JSONObject(body).optInt("graderId", -1);

        if(roleID == -1 || graderID == -1){
            return new JSONObject("error", "Missing roleId or graderId").toString();
        }

        RoleGraderPermission.deletePermission(roleID, graderID);

        return "ok";
    }

    @GET
    @Path("/getAllPermissions")
    @Produces(MediaType.APPLICATION_JSON)
    public String getAllPermissions(){
        System.out.println("[getAllPermission]");

        List<RoleGraderPermission> permissions = RoleGraderPermission.getAllPermissions();

        JSONArray ret = new JSONArray();

        for(RoleGraderPermission permission : permissions)
            ret.put(permission.toJSON());

        return ret.toString();
    }


}
