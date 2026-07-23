package Bot.ShaxzadBot.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;

@Entity
@Table(name = "telegram_users")
public class TelegramUser {

    @Id
    @Column(name = "telegram_user_id", nullable = false)
    private Long telegramUserId;

    @Column(name = "chat_id", nullable = false)
    private Long chatId;

    @Column(name = "username", length = 255)
    private String username;

    @Column(name = "first_name", length = 255)
    private String firstName;

    @Column(name = "last_name", length = 255)
    private String lastName;

    @Column(name = "phone_number", nullable = false, length = 32)
    private String phoneNumber;

    @Column(name = "registered_at", nullable = false)
    private OffsetDateTime registeredAt;

    protected TelegramUser() {
    }

    public TelegramUser(Long telegramUserId, Long chatId, String username,
                        String firstName, String lastName, String phoneNumber,
                        OffsetDateTime registeredAt) {
        this.telegramUserId = telegramUserId;
        this.chatId = chatId;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.registeredAt = registeredAt;
    }

    public Long getTelegramUserId() {
        return telegramUserId;
    }

    public Long getChatId() {
        return chatId;
    }

    public String getUsername() {
        return username;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public OffsetDateTime getRegisteredAt() {
        return registeredAt;
    }

    public void updateProfile(Long chatId, String username, String firstName,
                              String lastName, String phoneNumber) {
        this.chatId = chatId;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
    }
}
