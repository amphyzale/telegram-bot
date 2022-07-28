package com.bscideas.telegrambot.bot.manager.impl;

import com.bscideas.telegrambot.bot.manager.CommandManager;
import com.bscideas.telegrambot.bot.manager.CommandProcessor;
import com.bscideas.telegrambot.bot.manager.RequestData;
import com.bscideas.telegrambot.bot.manager.ResponseData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class CommandProcessorManagerImpl implements CommandProcessor {

    private final List<CommandManager> commands;

    @Override
    public ResponseData process(RequestData requestData) {
        return commands.stream()
                .filter(c -> c.supports(requestData.getCommand()))
                .map(c -> c.handleCommand(requestData))
                .findFirst()
                .orElseGet(() -> {
                    log.error("Can`t find available command for: {}", requestData.getCommand());
                    return null;
                });
    }
}
