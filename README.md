# DB GRADER 
#### (working title)

## Pokretanje pregledača
* U grader folderu:
```
docker build -t grader .
docker run -itd --name grader -p 51000:51000 --privileged=true  grader
docker exec -i grader /bin/bash -c './start.sh'
```

* Aplicakcija se pokreće na portu 51000 i može se testirati preko postmana (![paket za tesiranje](./grader.postman_collection.json)).

* Prilikom prvog pokretanja je potrebno poslati post zahtev za kreiranje baze (/createDatabase u postman kolekciji)
  * Ispratiti log da li je baza uspešno kreirana, ukoliko ne uspe iz nekoliko pokušaja može se kreirati ručno pokretanjem skripte unutar kontejnera:
  ```
  \\ otvranje terminala kontejnera: 
  docker exec -it grader bin/bash
  \\ samo db2inst1 user ima pristup bazi
  su - db2inst1
  cd /home/scripts/stud2020
  ./create.sh
  ```

## Pokretanje servera
* U server folderu:
```
docker build -t server .
docker run -itd --name server -p 52000:52000 --privileged=true --volume ./server_data:/database  server
```

* Setup u kontejneru:
```
docker exec -u db2inst1 -it -w /home  server /bin/bash
sh setup.sh 
```
* Pokretanje servera u kontjneru:
```
docker exec -u db2inst1 -it -w /home  server /bin/bash -c "java -jar server-1.0-SNAPSHOT-jar-with-dependencies.jar"
```

* Aplicakcija se pokreće na portu 52000 i može se testirati preko postmana

Da bi se aplikacija testirala potrebno je uneti podatke
* Za unošenje pregledača poslati POST request na `http://localhost:52000/grader/addGrader` sa zahtevom:
```
{
    "name": "stud2020",
    "endpoint": "http://172.17.0.2:51000", // adresa se dobija komandom docker inspect -f '{{range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}' grader  
    "active": true
}
```
* Dalje se mogu uneti zadaci slanjem POST zahteva na `http://localhost:52000/server/addTasks`
  Telo zahteva treba da izgleda ovako:
  ```
  {
    "graderId": 1, // id prethodno unetog pregledača, prvi put se može se dobiti slanjem GET zatehva na http://localhost:52000/grader/refreshGraders, naredni put se aktivni pregledači mogu dobiti slanjem GET zahteva za http://0.0.0.0:52000/grader/getGraders, ili svi pregledači (uključujući neaktivne) slanjem get zahteva na http://0.0.0.0:52000/grader/getAllGraders 
    "tasks": [
        {
            "task": "Izdvojiti podatke o svim studentima rođenim u Beogradu. Uzeti u obzir sve Beogradske opštine.",
            "solution": "SELECT * FROM DA.DOSIJE WHERE mestorodjenja LIKE 'Beograd%';",
            "ordering": "1"
        },
        {
            "task": "...",
            "solution": "...",
            "ordering": "..."
        },
    ]
  }
  ```
* Za testiranje pregledanja je potreban korisnik. On se može kreirati slanjem post zahteva na `http://localhost:52000/user/create`
  Telo zahteva treba da izgleda ovako:
  ```
  {
    "username": "student",
    "email": "student@gmail.com",
    "firstname": "...",
    "lastname": "...",
    "password": "...",
    "role": 3 // za studenta
  }
  ```
  * Zadatak se šalje na testiranje POST zahtevom na `http://localhost:52000/server/checkTask`
   Zahtev treba da ima naredno telo:
```
{
    "userId": "1", // id kreiranog korisnika, dobija se u odgovoru na kreiranje studenta, ili prilikom logovanja
    "taskId": "35", // id zadatka iz tabele tasks, spisak zahetva se može dobiti slanjem get zahteva na http://localhost:52000/page/refreshTasks, 
    "solution": "SELECT ... FROM ... WHERE ... "
}
```
Nakon što grader odgovori serveru rezultat će biti unet u tabelu submissions (id korisnika, id zadataka, da li je zadatak tačno rešen i sa kojom porukom.
* Logovanje predstavlja način da se dobije id korisnika iz baze, šalje se POST zahtev na `http://localhost:52000/user/login` sa telom zahteva:
```
{
    "username": "student",
    "password": "12345"
}
```
* Zadaci se mogu dobiti slanjem GET zahteva na `http://localhost:52000/page/refreshTasks` čime se zadatci dohvataju iz baze i unose u fajl. Dok god nema izmena u zadacima oni se mogu dohvatati sa `http://localhost:52000/page/getAllTasks`. Time se dohvataju zadaci dohvaćeni iz baze i sačuvani u fajl.

* Rang liste najbolje urađenih zadataka i najboljih korisnika se mogu dobiti slanjem GET zahteva `http://localhost:52000/page/refreshStats` za dohvatanje dorektno iz baze, ili slanjem GET zahteva na `http://localhost:52000/page/getStats` za dohvatanje prethodno sačuvanih rezultata.


* Generalni koraci u testiranju:
  * Pokrenuti grader
  * pokrenuti server
  * proveriti da server ima pregledač sa odgovrajućim endpointom u tabeli graders, i da je taj pregledač u tabeli aktivan
  * proveriti da se zadatak za pregledanje nalazi u tabeli tasks
  * proveriti da se korisnik nalazi u tabeli users
  * šalje se zahtev za pregledanje

 ### Load testing
U _server/src/test/java_ se nalazi _LoadTesting.java_ čijim pokretanjem se može poslati veći broj zahteva za proveru koje opterećenje sistem može da podnese. Ta aplikacija koristi fajl _server/src/test/correct_queries.json_. Fajl treba da sadrži rešenja zadataka za odgovarajuće id-eve iz tabele (verovatno će biti potrebne izmene). U tom fajlu treba da se nađu rešenja zadataka koja će se slati na testiranje. Rešenja ne moraju biti tačna, ali imati u vidu da se rešenja sa sintaksnim greškama izvršavaju brže nego tačna rešenja, pa test neće uzeti dovoljno vremena. Aplikacija će napraviti odgovarajući broj test korisnika i slati zahteve graderu. Pre pokretanja proveriti da u tabeli users nema korisnika čiji username počinje sa _test_, ukoliko ima obrisati ih. U suprotnom kreiranje izvesnog broja novih korisnika za test neće uspetu jer je korisničko ime već zauzeto.
