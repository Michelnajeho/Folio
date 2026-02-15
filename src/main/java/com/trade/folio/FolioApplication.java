package com.trade.folio;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.trade.folio.domain.**.mapper")
public class FolioApplication {

    public static void main(String[] args) {
        SpringApplication.run(FolioApplication.class, args);
    }

}
