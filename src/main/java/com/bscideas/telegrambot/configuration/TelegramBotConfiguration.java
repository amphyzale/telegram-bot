package com.bscideas.telegrambot.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "telegram-bot")
public class TelegramBotConfiguration {


    @Value("${username}")
    private String name;

    @Value("${token}")
    private String token;

    private List<String> adminUsernameList;
}
