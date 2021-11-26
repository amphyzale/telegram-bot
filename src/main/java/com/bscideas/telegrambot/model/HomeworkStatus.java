package com.bscideas.telegrambot.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum HomeworkStatus {

    WAITING("В ожидании проверки"),
    ON_CHECK("На проверке"),
    ON_CORRECTIONS("Исправление замечаний"),
    DONE("Готово");

    private final String text;
}
