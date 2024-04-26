package org.example.Conreollers;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import org.example.Tables.RoleGraderPermission;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

@Path("/roleGraderPermission")
public class RoleGraderPermissionController {
    @POST
    @Path("/addPermission")
    @Produces(MediaType.APPLICATION_JSON)
    public String addPermission(String body){
        System.out.println("[addPermission]");
        JSONArray permissionsFromRequest = new JSONArray(body);

        List<RoleGraderPermission> permissions = new ArrayList<>();
        JSONArray errors = new JSONArray();
        for (Integer i=0; i<permissionsFromRequest.length(); i++) {
            JSONObject reqPermission = permissionsFromRequest.getJSONObject(i);

            Integer roleID = reqPermission.optInt("roleId", -1);
            Integer graderID = reqPermission.optInt("graderId", -1);

            if (roleID == -1 || graderID == -1) {
                errors.put("Missing roleId or graderId, req no. " + i);
                continue;
            }

            permissions.add(new RoleGraderPermission(roleID, graderID));
        }

        String insertErrors = RoleGraderPermission.insertPermissions(permissions);
        errors.put(insertErrors);

        PageController.refreshTasks();

        return new JSONObject().put("message", "ok").put("error", errors).toString();
    }
    @POST
    @Path("/removePermission")
    @Produces(MediaType.APPLICATION_JSON)
    public String removePermission(String body){
        System.out.println("[removePermission]");
        JSONArray permissionsFromRequest = new JSONArray(body);

        List<RoleGraderPermission> permissions = new ArrayList<>();
        JSONArray errors = new JSONArray();
        for (Integer i=0; i<permissionsFromRequest.length(); i++) {
            JSONObject reqPermission = permissionsFromRequest.getJSONObject(i);

            Integer roleID = reqPermission.optInt("roleId", -1);
            Integer graderID = reqPermission.optInt("graderId", -1);

            if (roleID == -1 || graderID == -1) {
                errors.put("Missing roleId or graderId, req no. " + i);
                continue;
            }

            permissions.add(new RoleGraderPermission(roleID, graderID));
        }

        String deleteErrors = RoleGraderPermission.deletePermissions(permissions);
        errors.put(deleteErrors);

        PageController.refreshTasks();

        return new JSONObject().put("message", "ok").put("error", errors).toString();
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
