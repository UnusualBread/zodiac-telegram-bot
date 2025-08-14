package com.unusualbread.zodiacbot;

import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;

import java.nio.file.Files;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args) throws Exception {
        var botToken = Files.readString(Path.of("bot.txt")).trim();

        try (var botsApplication = new TelegramBotsLongPollingApplication()) {
            botsApplication.registerBot(botToken, new ZodiacSignBot(botToken));

            System.out.println("ZodiacSignBot successfully started!");

            Thread.currentThread().join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}