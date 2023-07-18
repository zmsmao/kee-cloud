package com.kee.model.rocketmq.producer;


import com.kee.common.security.annotation.EnableCustomConfig;
import com.kee.common.security.annotation.EnableKeeFeignClients;

import com.kee.common.swagger.annotation.EnableCustomSwagger2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableCustomConfig
@EnableCustomSwagger2
@EnableKeeFeignClients
@EnableScheduling
@SpringBootApplication
public class KeeModelProducerApplication {
    public static void main(String[] args) {
        SpringApplication.run(KeeModelProducerApplication.class,args);
        System.out.println("启动成功"+"\n" +
                " ________   ___      ___   ________      __   ___  _______   _______  \n" +
                "(\"      \"\\ |\"  \\    /\"  | /\"       )    |/\"| /  \")/\"     \"| /\"     \"| \n" +
                " \\___/   :) \\   \\  //   |(:   \\___/     (: |/   /(: ______)(: ______) \n" +
                "   /  ___/  /\\\\  \\/.    | \\___  \\       |    __/  \\/    |   \\/    |   \n" +
                "  //  \\__  |: \\.        |  __/  \\\\      (// _  \\  // ___)_  // ___)_  \n" +
                " (:   / \"\\ |.  \\    /:  | /\" \\   :)     |: | \\  \\(:      \"|(:      \"| \n" +
                "  \\_______)|___|\\__/|___|(_______/      (__|  \\__)\\_______) \\_______) \n" +
                "                                                                      \n");
    }
}