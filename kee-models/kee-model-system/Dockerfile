FROM openjdk:11

MAINTAINER zms<1974525360.qq.com>

WORKDIR /app

ADD kee-model-system.jar /app/kee-model-system.jar

VOLUME /app/logs

EXPOSE 9201

ENTRYPOINT ["java","-jar","kee-model-system.jar"]