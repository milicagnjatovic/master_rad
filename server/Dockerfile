FROM ibmcom/db2:latest

ENV DB2INST1_PASSWORD pass
ENV LICENSE accept
ENV PRIVILEGED true

RUN yes | yum install java-11-openjdk

COPY ./target/server-1.0-SNAPSHOT-jar-with-dependencies.jar /home
COPY ./resources  /home

EXPOSE 52000

VOLUME /database

HEALTHCHECK --interval=5m --timeout=3s \
  CMD su - ${DB2INSTANCE} -c "db2 connect to sample"

