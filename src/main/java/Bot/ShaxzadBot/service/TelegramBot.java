package Bot.ShaxzadBot.service;

import Bot.ShaxzadBot.config.BotConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.HashMap;
import java.util.Map;

@Component
public class TelegramBot extends TelegramLongPollingBot {

    private static final Logger logger =
            LoggerFactory.getLogger(TelegramBot.class);

    private final BotConfig config;

    private final Map<String, String> atmDatabase = new HashMap<>();

    public TelegramBot(BotConfig config) {
        this.config = config;

        atmDatabase.put("44444", "📍 Алматы, ул. Абая 10, Зона А");
        atmDatabase.put("12345", "📍 Астана, ул. Туран 15");
        atmDatabase.put("99999", "📍 Шымкент, ул. Байдибек би 20");
    }

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    @Override
    public String getBotToken() {
        return config.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {

        if (update.hasMessage() && update.getMessage().hasText()) {

            String chatId = update.getMessage().getChatId().toString();
            String text = update.getMessage().getText().trim();

            String response;

            if (text.matches("\\d+")) {

                if (atmDatabase.containsKey(text)) {
                    response = "🏧 ATM " + text + "\n" +
                            atmDatabase.get(text);
                } else {
                    response = "❌ ATM с таким кодом не найден";
                }

            } else {
                response = "Введите код ATM (только цифры)";
            }

            SendMessage message = new SendMessage();
            message.setChatId(chatId);
            message.setText(response);

            try {
                execute(message);
            } catch (Exception e) {
                logger.error("Ошибка отправки сообщения", e);
            }
        }
    }
}