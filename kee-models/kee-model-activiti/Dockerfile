FROM openjdk:11

MAINTAINER zms<1974525360.qq.com>

WORKDIR /app

ADD kee-model-activiti.jar /app/kee-model-activiti.jar

VOLUME /app/logs

EXPOSE 9204

ENTRYPOINT ["java","-jar","kee-model-activiti.jar"]