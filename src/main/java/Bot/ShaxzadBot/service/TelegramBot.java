package Bot.ShaxzadBot.service;

import Bot.ShaxzadBot.config.BotConfig;
import Bot.ShaxzadBot.entity.Atm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Contact;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.List;

@Component
public class TelegramBot extends TelegramLongPollingBot {
    private static final Logger logger = LoggerFactory.getLogger(TelegramBot.class);

    private final BotConfig config;
    private final PprService pprService;
    private final AtmService atmService;
    private final TelegramUserService telegramUserService;

    public TelegramBot(BotConfig config, PprService pprService, AtmService atmService,
                       TelegramUserService telegramUserService) {
        this.config = config;
        this.pprService = pprService;
        this.atmService = atmService;
        this.telegramUserService = telegramUserService;
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
        try {
            if (!update.hasMessage()) {
                return;
            }

            Message message = update.getMessage();
            if (message.hasContact()) {
                handleContact(message);
                return;
            }

            if (!message.hasText()) {
                return;
            }

            String text = message.getText().trim();
            Long chatId = message.getChatId();
            Long senderId = message.getFrom() != null ? message.getFrom().getId() : null;

            logger.info("Received message: '{}' from user: {} in chat: {}", text, senderId, chatId);

            if ("/start".equalsIgnoreCase(text)) {
                handleStart(chatId, senderId);
                return;
            }

            if (senderId == null || !telegramUserService.isRegistered(senderId)) {
                sendText(chatId, "Пожалуйста, нажмите /start и сначала поделитесь своими контактными данными.", buildContactKeyboard());
                return;
            }

            if (!text.matches("\\d+")) {
                sendText(chatId, "Отправьте номер АТМ, используя только цифры.");
                return;
            }

            String response = atmService.findByNumber(text)
                    .map(this::buildAtmResponse)
                    .orElse("АТМ с этим кодом не найден.");

            sendText(chatId, response);
        } catch (Exception e) {
            logger.error("Error processing update", e);
        }
    }

    public void sendBroadcast(Long chatId, String response) {
        sendText(chatId, response);
    }

    private void handleStart(Long chatId, Long senderId) {
        if (senderId != null && telegramUserService.isRegistered(senderId)) {
            sendText(chatId, "Вы уже зарегистрированы. Отправьте номер АТМ.", new ReplyKeyboardRemove(true));
            return;
        }

        sendText(chatId, "Нажмите кнопку ниже и поделитесь своими контактными данными.", buildContactKeyboard());
    }

    private void handleContact(Message message) {
        Long chatId = message.getChatId();
        Long senderId = message.getFrom() != null ? message.getFrom().getId() : null;
        Contact contact = message.getContact();

        if (senderId == null) {
            sendText(chatId, "Не удалось определить пользователя. Пожалуйста, снова нажмите /start.");
            return;
        }

        if (contact.getUserId() != null && !contact.getUserId().equals(senderId)) {
            sendText(chatId, "Поделитесь своими контактными данными с помощью кнопки.", buildContactKeyboard());
            return;
        }

        telegramUserService.registerFromContact(message);
        sendText(chatId, "Регистрация завершена. Теперь отправьте номер АТМ.", new ReplyKeyboardRemove(true));
    }

    private String buildAtmResponse(Atm atm) {
        List<String> kit = pprService.getKitByModel(atm.getModel());

        StringBuilder kitText = new StringBuilder("\n\nPPR kit:\n");
        for (String item : kit) {
            kitText.append("- ").append(item).append("\n");
        }

        return   "🌍 Регион: " + atm.getRegion() + "\n"+
                "🏧 №ATM: " + atm.getNumber() + "\n" +
                "🖥️ Модель: " + atm.getModel() + "\n" +
                "🏢 Организация: " + atm.getOrganization() + "\n" +
                "📍 Адрес: " + atm.getAddress() + "\n" +
                "🗂️ Сектор: " + atm.getSector() + "\n" +

                kitText;
    }

    private ReplyKeyboardMarkup buildContactKeyboard() {
        KeyboardButton button = new KeyboardButton();
        button.setText("Поделись с контактом");
        button.setRequestContact(true);

        KeyboardRow row = new KeyboardRow();
        row.add(button);

        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        markup.setKeyboard(List.of(row));
        markup.setResizeKeyboard(true);
        markup.setOneTimeKeyboard(true);
        markup.setSelective(true);
        return markup;
    }

    private void sendText(Long chatId, String response) {
        sendText(chatId, response, null);
    }

    private void sendText(Long chatId, String response, ReplyKeyboard replyKeyboard) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(response);
        if (replyKeyboard != null) {
            message.setReplyMarkup(replyKeyboard);
        }

        try {
            execute(message);
        } catch (Exception e) {
            logger.error("Ошибка при отправке сообщения", e);
        }
    }
}
