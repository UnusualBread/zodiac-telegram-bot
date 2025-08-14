package com.unusualbread.zodiacbot;

import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.time.Month;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ZodiacSignBot implements LongPollingSingleThreadUpdateConsumer {

    private final TelegramClient telegramClient;

    private long chatId;

    public ZodiacSignBot(String botToken) {
        telegramClient = new OkHttpTelegramClient(botToken);
    }

    @Override
    public void consume(Update update) {
        //var chatId = 1L;
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        if (update.hasCallbackQuery()) {
            try {
                telegramClient.execute(AnswerCallbackQuery.builder()
                        .callbackQueryId(update.getCallbackQuery().getId())
                        .build());
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }

            try {
                telegramClient.execute(DeleteMessage.builder()
                        .chatId(chatId)
                        .messageId(update.getCallbackQuery().getMessage().getMessageId())
                        .build());
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }

            var data = update.getCallbackQuery().getData();
            System.out.println("User id: " + update.getCallbackQuery().getFrom().getId() + "\nFirst name: " + update.getCallbackQuery().getFrom().getFirstName() + "\nLast name: " + update.getCallbackQuery().getFrom().getLastName() + "\nUsername: " + update.getCallbackQuery().getFrom().getUserName());
            System.out.println("Has callback query: " + update.hasCallbackQuery());
            System.out.println("Callback data: " + data);

            if ("again".equals(data)) {
                showSelectMonthDialog();
            }

            if (data.matches("\\d{1,2}")) {
                System.out.println("Selected month");
                var month = Month.values()[Integer.parseInt(data) - 1];

                var replyMarkup = createMonthDaysReplyMarkup(month);

                buildAndSendMessage("Выберите день:", replyMarkup);

                return;
            }

            if (data.matches("\\d{2}\\.\\d{2}")) {
                System.out.println("Selected day");
                int day = Integer.parseInt(data.substring(0, data.indexOf('.')));

                int month = Integer.parseInt(data.substring(data.indexOf('.') + 1));

                String signName = ZodiacUtils.getSignName(day, month);

                var replyMarkup = createAgainMarkup();

                buildAndSendMessage("Знак зодиака: " + signName, replyMarkup);

                System.out.println("Success!\n");

                return;
            }
        }

        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();

            this.chatId = update.getMessage().getChatId();

            if ("/start".equals(messageText)) {
                buildAndSendMessage("Добро пожаловать в бота, который подскажет знак зодиака по выбранной дате.");
                showSelectMonthDialog();
                return;
            }

            buildAndSendMessage("Бот пока не умеет отвечать на сообщения.");
        }
    }

    private void showSelectMonthDialog() {
        var replyMarkup = createMonthReplyMarkup();
        buildAndSendMessage("Выберите месяц:", replyMarkup);
    }

    private InlineKeyboardMarkup createMonthReplyMarkup() {
        var month = Month.values();

        var keyboardRows = new ArrayList<InlineKeyboardRow>();

        for (int i = 0; i < 6; i++) {
            var month1Button = createMonthButton(month[i]);
            var month2Button = createMonthButton(month[i + 6]);

            keyboardRows.add(new InlineKeyboardRow(month1Button, month2Button));
        }

        return new InlineKeyboardMarkup(keyboardRows);
    }

    private InlineKeyboardMarkup createMonthDaysReplyMarkup(Month month) {
        var keyboardRows = new ArrayList<InlineKeyboardRow>();

        for (int i = 0; i < 7; i++) {
            var keyboardRow = new InlineKeyboardRow();
            for (int j = 0; j < 5; j++) {
                var day = i + 1 + j * 7;

                var buttonText = day <= month.maxLength() ? String.valueOf(day) : "x";

                var dayButton = new InlineKeyboardButton(buttonText);
                //dayButton.setCallbackData(buttonText + '.' + month.getValue());

                String formatted = String.format("%02d.%02d", day, month.getValue());
                dayButton.setCallbackData(formatted);

                keyboardRow.add(dayButton);
            }

            keyboardRows.add(keyboardRow);
        }

        return new InlineKeyboardMarkup(keyboardRows);
    }

    private InlineKeyboardMarkup createAgainMarkup() {
        var againButton = new InlineKeyboardButton("Выбрать другую дату");
        againButton.setCallbackData("again");

        return new InlineKeyboardMarkup(List.of(new InlineKeyboardRow(againButton)));
    }

    private static InlineKeyboardButton createMonthButton(Month month) {
        var monthButton = new InlineKeyboardButton(getMonthDisplayName(month, TextStyle.FULL_STANDALONE));
        monthButton.setCallbackData(String.valueOf(month.getValue()));

        return monthButton;
    }

    private static String getMonthDisplayName(Month month, TextStyle textStyle) {
        return month.getDisplayName(textStyle, Locale.forLanguageTag("ru"));
    }

    public SendMessage buildMessage(String text) {
        SendMessage message = SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .build();
        return message;
    }

    public SendMessage buildMessage(String text, InlineKeyboardMarkup replyMarkup) {
        SendMessage message = SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .replyMarkup(replyMarkup)
                .build();
        return message;
    }

    public void sendMessage(SendMessage message) {
        try {
            telegramClient.execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void buildAndSendMessage(String text) {
        SendMessage message = buildMessage(text);
        sendMessage(message);
    }

    public void buildAndSendMessage(String text, InlineKeyboardMarkup replyMarkup) {
        SendMessage message = buildMessage(text, replyMarkup);
        sendMessage(message);
    }
}