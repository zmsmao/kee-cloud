FROM openjdk:11

MAINTAINER zms<1974525360.qq.com>

WORKDIR /app

ADD kee-auth.jar /app/kee-auth.jar

VOLUME /app/logs

EXPOSE 9104

ENTRYPOINT ["java","-jar","kee-auth.jar"]