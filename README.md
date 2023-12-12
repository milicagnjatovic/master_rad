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

* Generalni koraci u testiranju:
  * generateSolution preko postmana
  * provera da li fajlovi izgledaju očekivano sa getSolution pozivom za id nekog od generisanih zadataka (da se nije desilo da fali shema, tabela, konekcija...)
  * checkSolution pozivi za proveru studentskog rešenja
