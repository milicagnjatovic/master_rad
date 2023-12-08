chown db2inst1 home
sudo su - db2inst1 -c "mkdir -p /home/student_results"
#mkdir /home/student_results
sudo su - db2inst1 -c "cd /home && java -jar grader-1.0-SNAPSHOT-jar-with-dependencies.jar"