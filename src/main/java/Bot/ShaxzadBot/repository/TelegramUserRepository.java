package Bot.ShaxzadBot.repository;

import Bot.ShaxzadBot.entity.TelegramUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TelegramUserRepository extends JpaRepository<TelegramUser, Long> {
}
