package com.bscideas.telegrambot.model;

public enum HomeworkStatus {

    WAITING("В ожидании проверки"),
    ON_CHECK("На проверке"),
    ON_CORRECTIONS("Исправление замечаний"),
    DONE("Готово");

    HomeworkStatus(String status) {
    }
}
