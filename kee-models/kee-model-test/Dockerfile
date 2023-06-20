FROM openjdk:11

MAINTAINER zms<1974525360.qq.com>

WORKDIR /app

ADD kee-model-test.jar /app/kee-model-test.jar

VOLUME /app/logs

EXPOSE 9206

ENTRYPOINT ["java","-jar","kee-model-test.jar"]