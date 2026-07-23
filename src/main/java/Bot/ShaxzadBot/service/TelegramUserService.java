package Bot.ShaxzadBot.service;

import Bot.ShaxzadBot.entity.TelegramUser;
import Bot.ShaxzadBot.repository.TelegramUserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;

import java.time.OffsetDateTime;

@Service
public class TelegramUserService {

    private final TelegramUserRepository telegramUserRepository;

    public TelegramUserService(TelegramUserRepository telegramUserRepository) {
        this.telegramUserRepository = telegramUserRepository;
    }

    public boolean isRegistered(Long telegramUserId) {
        return telegramUserRepository.existsById(telegramUserId);
    }

    @Transactional
    public TelegramUser registerFromContact(Message message) {
        if (!message.hasContact()) {
            throw new IllegalArgumentException("Message does not contain contact data");
        }

        User sender = message.getFrom();
        if (sender == null) {
            throw new IllegalStateException("Telegram sender is missing");
        }

        if (message.getContact().getUserId() != null
                && !message.getContact().getUserId().equals(sender.getId())) {
            throw new IllegalArgumentException("Shared contact does not belong to the sender");
        }

        TelegramUser user = telegramUserRepository.findById(sender.getId())
                .orElseGet(() -> new TelegramUser(
                        sender.getId(),
                        message.getChatId(),
                        sender.getUserName(),
                        sender.getFirstName(),
                        sender.getLastName(),
                        message.getContact().getPhoneNumber(),
                        OffsetDateTime.now()
                ));

        user.updateProfile(
                message.getChatId(),
                sender.getUserName(),
                sender.getFirstName(),
                sender.getLastName(),
                message.getContact().getPhoneNumber()
        );

        return telegramUserRepository.save(user);
    }
}
