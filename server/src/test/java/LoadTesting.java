import org.example.Conreollers.TaskController;
import org.example.Tables.Grader;
import org.example.Tables.Submission;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Time;
import java.util.*;
import java.util.concurrent.*;

public class LoadTesting {
    public static void main(String[] args) throws ExecutionException, InterruptedException, IOException {
        Grader.retrieveGradersToMap();

//        test1();

//        new LoadTesting().test("/home/milica/Desktop/master_rad/server/src/test/correct_queries.json", 5, 5);
        new LoadTesting().test("correct_queries.json", 100, 100);
    }
    public static void test1() throws ExecutionException, InterruptedException {
        String payload = " {\n" +
                "    \"userid\": \"2\",\n" +
                "    \"taskid\": \"31\",\n" +
                "    \"solution\": \"with tmp as (  select indeks, idpredmeta, count(distinct skgodina) as br_upisa  from da.upisankurs uk  group by indeks, idpredmeta ) (select distinct d.indeks, ime||' '||prezime as \\\"Ime i prezime\\\", sp.naziv as komentar,  case   when dayname(d.datupisa) is not null then dayname(d.datupisa)   when dayname(i.datpolaganja) is not null then dayname(i.datpolaganja)  else null end as \\\"Naziv dana\\\" from da.dosije d join da.studentskistatus ss on d.idstatusa = ss.id and ss.naziv='Budzet' join tmp on d.indeks = tmp.indeks and br_upisa>4 join da.studijskiprogram sp on d.idprograma = sp.id join da.ispit i on d.indeks = i.indeks and tmp.idpredmeta=i.idpredmeta union select distinct d.indeks, ime||' '||prezime as \\\"Ime i prezime\\\",  p.naziv||' '||case when i.ocena>5 and status='o' then ocena else 5 end as komentar,  case   when dayname(d.datupisa) is not null then dayname(d.datupisa)   when dayname(i.datpolaganja) is not null then dayname(i.datpolaganja)  else null end as \\\"Naziv dana\\\" from da.dosije d  join tmp on d.indeks = tmp.indeks and br_upisa=5 join da.ispit i on d.indeks = i.indeks and tmp.idpredmeta=i.idpredmeta join da.predmet p on i.idpredmeta=p.id) order by indeks asc, 4 desc;    \"\n" +
                "  }";
        ExecutorService executor = Executors.newFixedThreadPool(5);

        List<Future<String>> futures = new ArrayList<>();

        Date start = new Date();
        for (int i = 0; i < 10; i++) {
            Future<String> future = executor.submit(() -> {
                TaskController controller = new TaskController();
                return controller.checkTask(payload);
            });
            futures.add(future);
        }

        // Shutdown the executor to prevent new tasks from being submitted
        executor.shutdown();

        // Wait for all tasks to complete and collect their results
        for (Future<String> future : futures) {
            String result = future.get();
            System.out.println("Result: " + result);
        }
        Date end = new Date();

        System.out.println("Time difference " + (end.getTime() - start.getTime()));
    }

    public void test(String path, Integer noThreads, Integer noRequests) throws ExecutionException, InterruptedException, IOException {
        String payloads = String.join("", Files.readAllLines(Path.of(path)));
        JSONArray arr = new JSONArray(payloads);
        List<Integer> userIds = Arrays.asList(22,23,24,25,26,41,42,43,44,45);

        ExecutorService executor = Executors.newFixedThreadPool(noThreads);

        List<Future<String>> futures = new ArrayList<>();

        Date start = new Date();
        int noSentRequests = 0;
        for (int i = 0; i < arr.length(); i++) {
            for (int j = 0; j < userIds.size(); j++) {
                Integer userId = userIds.get(j);
                JSONObject req = arr.getJSONObject(i);
                req.put("userid", userId);

                final JSONObject request = new JSONObject(req, JSONObject.getNames(req));

                Future<String> future = executor.submit(() -> {
                    TaskController controller = new TaskController();
                    return controller.checkTask(String.valueOf(request));
                });
                futures.add(future);

                noSentRequests++;
                if (noRequests==noSentRequests)
                    break;
            }

            if (noRequests==noSentRequests)
                break;
        }

        executor.shutdown();

//        // Wait for all tasks to complete and collect their results
        for (Future<String> future : futures) {
            String result = future.get();
            System.out.println("Result: " + result);
        }
        Date end = new Date();

        System.out.println("Time difference " + (end.getTime() - start.getTime()));
    }
}
