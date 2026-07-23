package Bot.ShaxzadBot.config;

import Bot.ShaxzadBot.service.TelegramBot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Component
public class BotInitializer {

    private static final Logger logger =
            LoggerFactory.getLogger(BotInitializer.class);
    private static final long RETRY_DELAY_MS = 30_000L;

    @Autowired
    TelegramBot bot;

    @Value("${bot.enabled:true}")
    private boolean botEnabled;

    @EventListener(ApplicationReadyEvent.class)
    public void init() {

        if (!botEnabled) {
            logger.info("Telegram bot registration is disabled");
            return;
        }

        if (bot.getBotToken() == null || bot.getBotToken().isBlank()
                || bot.getBotUsername() == null || bot.getBotUsername().isBlank()) {
            logger.warn("Telegram bot is not registered: set BOT_NAME and BOT_KEY");
            return;
        }

        Thread botStarter = new Thread(this::registerWithRetry, "telegram-bot-starter");
        botStarter.setDaemon(false);
        botStarter.start();
    }

    private void registerWithRetry() {
        TelegramBotsApi telegramBotsApi;
        try {
            telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        } catch (TelegramApiException e) {
            logger.error("Failed to create TelegramBotsApi", e);
            return;
        }

        while (!Thread.currentThread().isInterrupted()) {
            try {
                telegramBotsApi.registerBot(bot);
                logger.info("Telegram bot registered successfully");
                return;
            } catch (TelegramApiException e) {
                logger.error("Error registering bot, will retry in {} ms", RETRY_DELAY_MS, e);
                try {
                    Thread.sleep(RETRY_DELAY_MS);
                } catch (InterruptedException interruptedException) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }
    }
}
