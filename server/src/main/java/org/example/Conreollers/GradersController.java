package org.example.Conreollers;

import jakarta.ws.rs.*;
import org.example.Tables.Grader;
import org.json.JSONObject;

import javax.ws.rs.core.MediaType;

@Path("/grader")
public class GradersController {
    /**
     *
     * @return Vraća JSON listu aktivnih pregledača. Svaki pregledač ima name i id. Endpoint se ne vraća da bi se izbegao rizik da klijent direktno kontaktira pregledač.
     */
    @GET
    @Path("/getGraders")
    @Produces(MediaType.APPLICATION_JSON)
    public String getGraders(){
        System.out.println("[getGraders]");
        return Grader.gradersString;
    }

    /**
     *
     * @return Vraća listu aktivnih i neaktivnih pregledača.Za svaki pregledač vraća id, name, endpoint i da li je active. Nije namenjeno studentima i ostalim korisnicima.
     */
    @GET
    @Path("/getAllGraders")
    @Produces(MediaType.APPLICATION_JSON)
    public String getAllGraders(){
        System.out.println("[getAllGraders]");
        return Grader.getAllGraders();
    }

    /**
     *
     * @return Vraća aktivne gradere kao i /getGraders. Ovim zahtevom će se pregledači dohvatiti iz baze i obnoviti string sa pregledačima (Grader.gradersString)
     */
    @GET
    @Path("/refreshGraders")
    @Produces(MediaType.APPLICATION_JSON)
    public String refreshGraders(){
        System.out.println("[refreshGraders]");
        Grader.retrieveGradersToMap();
        return Grader.gradersString;
    }

    /**
     * Zahtevom se u tabelu pregledača (Graders) unosi novi pregledač.
     *
     * @param body
     * Telo zahteva treba da bude u narednom formatu:
     <pre>
    {
    "name": "naziv pregledača",
    "endpoint": "adresa na koju se šalje zahtev za pregledanje",
    "active": true // da li je pregledač aktivan
    }
    Sva polja su obavezna.
    </pre>

     * @return Vraća aktine pregledače kao i /getGraders
     */
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

    /**
     * Zahtevom se u menjaju vrenodsit u tabeli pregledača (Graders). Primarno namenjeno (de)akivaciji pregledača, izmeni adrese na kojoj se pregledač nalazi.
     * @param body
     * Telo zahteva treba da bude u narednom formatu:
    <pre>
    {
    "id": 1, // id pregledača i bazi
    "name": "naziv pregledača",
    "endpoint": "adresa na koju se šalje zahtev za pregledanje",
    "active": true // da li je pregledač aktivan
    }
    Jedino id je obavezno polje.
    </pre>
     * @return Vraća aktine pregledače kao i /getGraders
     */
    @POST
    @Path("/updateGrader")
    @Consumes(MediaType.APPLICATION_JSON)
    public String updateGrader(String body){
        System.out.println("[updateGrader]");
        JSONObject request = new JSONObject(body);
        if (!request.has("id")){
           return new JSONObject("error", "Error | Id missing").toString();
        }
        Integer id = request.getInt("id");
        Grader grader = Grader.getById(id);
        if (grader == null){
            return new JSONObject("error", "Error | Grader does not exists").toString();
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
        return Grader.insertOrUpdateGrader(grader);
    }
}
