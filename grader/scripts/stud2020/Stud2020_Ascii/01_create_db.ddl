create database stud2020
	-- restrictive
	automatic storage yes
    --	on '/db2data' 
	-- dbpath on '/db2data' 
	using codeset utf-8 
	territory rs
	collate using system_1251_rs
	pagesize 4096
	with 'Nastavna baza 2020'
;

update db cfg for stud2020
using 
	logfilsiz 4000 
	logprimary 3 
	logsecond 40
immediate
;

connect to stud2020
;