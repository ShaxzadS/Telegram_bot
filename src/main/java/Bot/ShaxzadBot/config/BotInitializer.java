package Bot.ShaxzadBot.config;

import Bot.ShaxzadBot.service.TelegramBot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Component
public class BotInitializer {

    private static final Logger logger =
            LoggerFactory.getLogger(BotInitializer.class);

    @Autowired
    TelegramBot bot;

    @Value("${bot.enabled:true}")
    private boolean botEnabled;

    @EventListener({ContextRefreshedEvent.class})
    public void init() {

        if (!botEnabled) {
            logger.info("Telegram bot registration is disabled");
            return;
        }

        if (bot.getBotToken() == null || bot.getBotToken().isBlank()
                || bot.getBotUsername() == null || bot.getBotUsername().isBlank()) {
            throw new IllegalStateException("Telegram bot is not registered: set BOT_NAME and BOT_KEY");
        }

        try {
            TelegramBotsApi telegramBotsApi =
                    new TelegramBotsApi(DefaultBotSession.class);

            telegramBotsApi.registerBot(bot);

        } catch (TelegramApiException e) {
            logger.error("Error registering bot", e);
        }
    }
}
