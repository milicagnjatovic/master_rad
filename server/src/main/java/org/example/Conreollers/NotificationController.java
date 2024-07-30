package org.example.Conreollers;

import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import org.example.FileUtil;
import org.example.Tables.Notification;
import org.json.JSONArray;
import org.json.JSONObject;

@Path("/notification")
public class NotificationController  {
    @POST
    @Path("new")
    public static String addNotification(String body){
        JSONObject requestObj = new JSONObject(body);
        if(!requestObj.has("title") || !requestObj.has("text"))
            return new JSONObject().put("error", "Title or text missing").toString();

        Notification notification = new Notification(requestObj.getString("title"), requestObj.getString("text"));

        Notification.insertNotification(notification);

        refreshNotifications();

        return new JSONObject().put("error", "").toString();
    }

    @POST
    @Path("delete")
    public static String deleteNotification(String body){
        JSONObject requestObj = new JSONObject(body);
        if(!requestObj.has("id"))
            return new JSONObject().put("error", "Id missing").toString();

        Integer id = requestObj.getInt("id");

        Notification.deleteNotification(id);

        refreshNotifications();

        return new JSONObject().put("error", "").toString();
    }

    @POST
    @Path("update")
    public static String updateNotification(String body){
        JSONObject requestObj = new JSONObject(body);
        if(!requestObj.has("id"))
            return new JSONObject().put("error", "Id missing").toString();

        Integer id = requestObj.getInt("id");

        Notification.updateNotification(id, requestObj.optString("text", null), requestObj.optString("title", null));

        refreshNotifications();
        return new JSONObject().put("error", "").toString();
    }

    private static void refreshNotifications(){
        JSONArray updatedNotifications = Notification.getNotificationsJSONArray(10);
        FileUtil.writeToFile(FileUtil.FILE_WITH_NOTIFICATIONS, updatedNotifications.toString());
    }
}
