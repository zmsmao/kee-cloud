package com.kee.model.file;

import com.kee.common.security.annotation.EnableCustomConfig;
import com.kee.common.security.annotation.EnableKeeFeignClients;
import com.kee.common.swagger.annotation.EnableCustomSwagger2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author Admin
 */
@EnableKeeFeignClients
@EnableCustomConfig
@EnableCustomSwagger2
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class KeeModelFileApplication {
    public static void main(String[] args) {
        SpringApplication.run(KeeModelFileApplication.class,args);
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