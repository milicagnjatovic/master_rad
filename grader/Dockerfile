FROM ibmcom/db2:latest

ENV DB2INST1_PASSWORD pass
ENV LICENSE accept
ENV PRIVILEGED true

RUN yes | yum install java-11-openjdk

COPY ./target/grader-1.0-SNAPSHOT-jar-with-dependencies.jar /home
COPY ./scripts home/scripts
COPY ./start.sh .


EXPOSE 51000

#CMD ["./start.sh"]
#CMD ["java", "-jar", "grader-1.0-SNAPSHOT-jar-with-dependencies.jar"]
HEALTHCHECK --interval=5m --timeout=3s \
  CMD su - ${DB2INSTANCE} -c "db2 connect to sample"

