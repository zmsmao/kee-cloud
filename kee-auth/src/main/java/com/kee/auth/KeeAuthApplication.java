package com.kee.auth;

import com.kee.common.security.annotation.EnableKeeFeignClients;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @Description : Object
 * @author zms
 */
@EnableKeeFeignClients
@SpringBootApplication
public class KeeAuthApplication {
    public static void main(String[] args) {
        SpringApplication.run(KeeAuthApplication.class,args);
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
