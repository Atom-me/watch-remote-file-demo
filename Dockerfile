FROM ubuntu:20.04
RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime
RUN apt-get update && \
    apt-get install -y openjdk-17-jdk && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

ENV JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64/
ENV PATH=$PATH:$JAVA_HOME/bin

ADD ./liveness.sh /app/liveness.sh
COPY ./target/watch-remote-file-demo-0.0.1-SNAPSHOT.jar /app/watch-remote-file-demo-0.0.1-SNAPSHOT.jar

ENTRYPOINT ["java","-jar","-Dspring.config.location=/app/config/application.yml","/app/watch-remote-file-demo-0.0.1-SNAPSHOT.jar","&"]
EXPOSE 9527