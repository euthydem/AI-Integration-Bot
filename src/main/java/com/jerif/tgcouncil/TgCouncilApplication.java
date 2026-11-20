package com.jerif.tgcouncil;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {
        "config", // ваш собственный пакет, где находится CompanionBot
        "org.telegram.telegrambots" // ключевой момент: позволить Spring сканировать пакет библиотеки
})
public class TgCouncilApplication {

    public static void main(String[] args) {
        SpringApplication.run(TgCouncilApplication.class, args);
    }

}
