FROM openjdk:11-jdk-slim
RUN apk update && apk add --no-cache libc6-compat && \
ln -s /lib/libc.musl-x86_64.so.1 /lib/ld-linux-x86-64.so.2
ARG JAR_FILE=target/*-allinone.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]