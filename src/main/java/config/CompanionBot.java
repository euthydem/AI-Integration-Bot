package config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;


@Component
public class CompanionBot implements SpringLongPollingBot, LongPollingSingleThreadUpdateConsumer {

    private final String botToken;
    private final ChatClient chatClient;
    private final TelegramClient telegramClient;
    private static final Logger log = LoggerFactory.getLogger(CompanionBot.class);

    public CompanionBot(@Value("${TELEGRAM_BOT_TOKEN}") String botToken,
                        ChatClient.Builder chatClientBuilder) {
        this.botToken = botToken;
        this.chatClient = chatClientBuilder.build();
        this.telegramClient = new OkHttpTelegramClient(botToken);
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public LongPollingUpdateConsumer getUpdatesConsumer() {
        return this;
    }

    @Override
    public void consume(Update update) {
        System.out.println(new java.io.File(".env").getAbsolutePath() + " exists=" + new java.io.File(".env").exists());
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            return;
        }

        String userMessage = update.getMessage().getText();
        long chatId = update.getMessage().getChatId();

        String aiResponse = chatClient.prompt()
                .user(userMessage)
                .call()
                .content();

        if (aiResponse == null || aiResponse.isBlank()) {
            aiResponse = "Sorry, I have no answer right now.";
        }

        SendMessage message = SendMessage.builder()
                .chatId(chatId)
                .text(aiResponse)
                .build();

        try {
            telegramClient.execute(message);
        } catch (TelegramApiException e) {
            log.error("Failed to send Telegram message", e);
        }
    }
}
