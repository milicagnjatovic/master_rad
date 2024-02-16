FROM ibmcom/db2:latest

ENV DB2INST1_PASSWORD pass
ENV LICENSE accept
ENV PRIVILEGED true

RUN yes | yum install java-11-openjdk

COPY ./target/grader-1.0-SNAPSHOT-jar-with-dependencies.jar /home
COPY ./scripts home/scripts
COPY ./start.sh .

# user for query execution
RUN useradd -m -s /bin/bash student && \
    echo 'student:matf2024.' | chpasswd

EXPOSE 51000

VOLUME /database

HEALTHCHECK --interval=5m --timeout=3s \
  CMD su - ${DB2INSTANCE} -c "db2 connect to sample"

