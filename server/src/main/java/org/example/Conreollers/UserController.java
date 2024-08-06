package org.example.Conreollers;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import org.example.FileUtil;
import org.example.Tables.Task;
import org.example.Tables.User;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.ws.rs.core.MediaType;
import java.security.NoSuchAlgorithmException;

@Path("/user")
public class UserController {
    /**
     * Zahtev za kreiranje novog korisnika.
     * @param body Telo zahteva treba da ima naredni format:
    <pre>
    {
        "username": "korisničko ime",
        "email": "mejl",
        "firstname": "ime",
        "lastname": "prezime",
        "password": "šifra",
        "role": 2 // id uloge
    }
    Jedini obavezni argumenti su username, email i password, ostalo je opciono.
    </pre>
     * @return Funkcija vraća JSON kreiranog korisnika
     */
    @POST
    @Path("/create")
    @Consumes(MediaType.APPLICATION_JSON)
    public String createUser(String body){
        return createOrUpdateUser(body).toString();
    }

    /**
     * Zahtev za izmenu postojećeg korisnika.
     * @param body Telo zahteva treba da ima naredni format:
    <pre>
    {
        "id": 261,
        "username": "korisničko ime",
        "email": "mejl",
        "firstname": "ime",
        "lastname": "prezime",
        "password": "šifra",
        "role": 2 // id uloge
    }
    Jedini obavezan argument je id korisnika, ostale je potrebno proslediti ukoliko se vrednost menja.
    </pre>
     * @return Funkcija vraća JSON izmenjenog korisnika
     */
    @POST
    @Path("/update")
    @Consumes(MediaType.APPLICATION_JSON)
    public String updateUser(String body){
        return createOrUpdateUser(body).toString();
    }

    /**
     *
     * @param body Telo zahteva treba da bude u narednom formatu:
    <pre>
    {
        "username": "korisničko ime",
        "password": "šifra"
    }
    </pre>
     * @return Funckija vraća JSON objekat ulogovanog korisnika.
     */
    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    public String login(String body){
        System.out.println("login");
        try {
            JSONObject user = User.login(new JSONObject(body));
            if (user.has("error"))
                return user.toString();

            JSONObject tasks = Task.getTasksForRole(user.optInt("roleId", -1));
            user.put("tasks", tasks);

            JSONArray notifications = new JSONArray(FileUtil.readFromFile(FileUtil.FILE_WITH_NOTIFICATIONS));
            user.put("notifications", notifications);

            JSONObject stats = new JSONObject(FileUtil.readFromFile(FileUtil.FILE_WITH_STATS));
            user.put("stats", stats);

            return user.toString();
        } catch (NoSuchAlgorithmException e) {
            return new JSONObject().put("error", e.getMessage()).toString();
        }
    }

    /**
     * Funkcija koja unosi novog korisnika ili menja postojećeg korisnika. Ukoliko je id korisnika prosleđen menja se postojeći update, inače se unosi novi.
     * @param body
     * @return Vraća JSON objekat sa novim korisnikom ili objekat sa porukom o grešci.
     */
    private static JSONObject createOrUpdateUser(String body){
        JSONObject request = new JSONObject(body);
        User user = null;
        try {
            user = new User(request);
            System.out.println(user);
        } catch (NoSuchAlgorithmException e) {
            return new JSONObject().put("error", e.getMessage());
        }

        // new user missing fields
        if (user.Id == null && (user.Username == null || user.Email == null || user.Password == null)){
            return new JSONObject().put("error", "Username, email or password missing.");
        }

        String response = "";
        if (user.Id == null)
            response = User.saveUser(user);
        else
            response = User.updateUser(user);

        if (response.isEmpty()){
            JSONObject ret = user.toJSON(request.getString("password"));
            JSONObject tasks = Task.getTasksForRole(user.Role.Id);
            ret.put("tasks", tasks);

            JSONArray notifications = new JSONArray(FileUtil.readFromFile(FileUtil.FILE_WITH_NOTIFICATIONS));
            ret.put("notifications", notifications);

            JSONObject stats = new JSONObject(FileUtil.readFromFile(FileUtil.FILE_WITH_STATS));
            ret.put("stats", stats);
            return ret;
        }
        return new JSONObject().put("error", response);
    }
}
