package com.atom.watchremotefile;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@MapperScan("com.atom.watchremotefile.mapper")
public class WatchRemoteFileDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(WatchRemoteFileDemoApplication.class, args);
    }

}
