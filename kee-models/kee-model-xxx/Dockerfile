FROM openjdk:11

MAINTAINER zms<1974525360.qq.com>

WORKDIR /app

ADD kee-model-xxx.jar /app/kee-model-xxx.jar

VOLUME /app/logs

EXPOSE 9209

ENTRYPOINT ["java","-jar","kee-model-xxx.jar"]