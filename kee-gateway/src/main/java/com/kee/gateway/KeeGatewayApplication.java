package com.kee.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * @Description : Object
 * @author zms
 */
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
public class KeeGatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(KeeGatewayApplication.class,args);
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
