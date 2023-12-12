db2 -tf 01_create_db.ddl
db2 -tf 02_drop_tables.ddl
db2 -tf 03_create_tables.ddl
db2 -tf 04_grant.ddl
db2 -tf 05_import_ixf.ddl
db2 connect reset

