FROM openjdk:11

MAINTAINER zms<1974525360.qq.com>

WORKDIR /app

ADD kee-gateway.jar /app/kee-gateway.jar

VOLUME /app/logs

EXPOSE 9102

ENTRYPOINT ["java","-jar","kee-gateway.jar"]