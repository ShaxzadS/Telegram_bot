package Bot.ShaxzadBot.service;

import Bot.ShaxzadBot.entity.TelegramUser;
import Bot.ShaxzadBot.repository.TelegramUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FaceIdReminderScheduler {

    private static final Logger logger = LoggerFactory.getLogger(FaceIdReminderScheduler.class);
    private static final String REMINDER_TEXT = "Не забудьте пройти Face ID.";
    private static final String TIME_ZONE = "Asia/Qyzylorda";

    private final TelegramUserRepository telegramUserRepository;
    private final TelegramBot telegramBot;

    public FaceIdReminderScheduler(TelegramUserRepository telegramUserRepository, TelegramBot telegramBot) {
        this.telegramUserRepository = telegramUserRepository;
        this.telegramBot = telegramBot;
    }

    @Scheduled(cron = "0 55 8 * * *", zone = TIME_ZONE)
    public void sendMorningReminder() {
        sendReminder("08:55");
    }

    @Scheduled(cron = "0 33 21 * * *", zone = TIME_ZONE)
    public void sendEveningReminder() {
        sendReminder("18:05");
    }

    private void sendReminder(String scheduleLabel) {
        List<TelegramUser> users = telegramUserRepository.findAll();
        if (users.isEmpty()) {
            logger.info("No registered users found for {} reminder", scheduleLabel);
            return;
        }

        int sentCount = 0;
        for (TelegramUser user : users) {
            Long chatId = user.getChatId();
            if (chatId == null) {
                continue;
            }

            try {
                telegramBot.sendBroadcast(chatId, REMINDER_TEXT);
                sentCount++;
            } catch (Exception e) {
                logger.error("Failed to send {} reminder to chat {}", scheduleLabel, chatId, e);
            }
        }

        logger.info("Sent {} reminder to {} users", scheduleLabel, sentCount);
    }
}
