package Bot.ShaxzadBot.service;

import Bot.ShaxzadBot.config.BotConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot {

    final BotConfig config;

    public TelegramBot(BotConfig config) {
        this.config = config;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String message = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            if ("/start".equals(message)) {
                startCommandReceived(chatId, update.getMessage().getChat().getFirstName());
            } else {
                sendMessage(chatId, message);
            }
        }
    }

    private void startCommandReceived(long chatId, String name) {
        String answer = "Привет, " + name + "! Я повторяю твои сообщения.";
        sendMessage(chatId, answer);
        log.info("Command received from user: " + name);
    }

    private void sendMessage(Long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(textToSend);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Ошибка отправки сообщения: ", e);
        }
    }

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    @Override
    public String getBotToken() {
        return config.getToken();
    }
}
