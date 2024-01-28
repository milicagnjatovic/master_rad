db2start
db2 -tf 01_drop_db.ddl


cd Stud2020_Ascii
./create.sh
cd ..

#cd Stud2020_utf8
#./create.sh
#cd ..

db2 -tf 02_explain_tables.ddl

