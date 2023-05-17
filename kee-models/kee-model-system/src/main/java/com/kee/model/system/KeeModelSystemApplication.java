package com.kee.model.system;

import com.kee.common.security.annotation.EnableCustomConfig;
import com.kee.common.security.annotation.EnableKeeFeignClients;
import com.kee.common.swagger.annotation.EnableCustomSwagger2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @Description : Object
 * @author zms
 */
@EnableCustomConfig
@EnableCustomSwagger2
@EnableKeeFeignClients
@EnableScheduling
@SpringBootApplication
public class KeeModelSystemApplication {
    public static void main(String[] args) {
        SpringApplication.run(KeeModelSystemApplication.class,args);
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
