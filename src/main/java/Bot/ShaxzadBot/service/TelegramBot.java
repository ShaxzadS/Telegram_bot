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

        if ("/start".equalsIgnoreCase(text)) {
            handleStart(chatId, senderId);
            return;
        }

        if (senderId == null || !telegramUserService.isRegistered(senderId)) {
            sendText(chatId, "Please press /start and share your contact first.", buildContactKeyboard());
            return;
        }

        if (!text.matches("\\d+")) {
            sendText(chatId, "Send the ATM number using digits only.");
            return;
        }

        String response = atmService.findByNumber(text)
                .map(this::buildAtmResponse)
                .orElse("ATM with this code was not found");

        sendText(chatId, response);
    }

    public void sendBroadcast(Long chatId, String response) {
        sendText(chatId, response);
    }

    private void handleStart(Long chatId, Long senderId) {
        if (senderId != null && telegramUserService.isRegistered(senderId)) {
            sendText(chatId, "You are already registered. Send the ATM number.", new ReplyKeyboardRemove(true));
            return;
        }

        sendText(chatId, "Press the button below and share your contact.", buildContactKeyboard());
    }

    private void handleContact(Message message) {
        Long chatId = message.getChatId();
        Long senderId = message.getFrom() != null ? message.getFrom().getId() : null;
        Contact contact = message.getContact();

        if (senderId == null) {
            sendText(chatId, "Could not identify the user. Please press /start again.");
            return;
        }

        if (contact.getUserId() != null && !contact.getUserId().equals(senderId)) {
            sendText(chatId, "Share your own contact using the button.", buildContactKeyboard());
            return;
        }

        telegramUserService.registerFromContact(message);
        sendText(chatId, "Registration completed. Now send the ATM number.", new ReplyKeyboardRemove(true));
    }

    private String buildAtmResponse(Atm atm) {
        List<String> kit = pprService.getKitByModel(atm.getModel());

        StringBuilder kitText = new StringBuilder("\n\nPPR kit:\n");
        for (String item : kit) {
            kitText.append("- ").append(item).append("\n");
        }

        return "ATM: " + atm.getNumber() + "\n" +
                "Model: " + atm.getModel() + "\n" +
                "Organization: " + atm.getOrganization() + "\n" +
                "Address: " + atm.getAddress() + "\n" +
                "Sector: " + atm.getSector() +
                kitText;
    }

    private ReplyKeyboardMarkup buildContactKeyboard() {
        KeyboardButton button = new KeyboardButton();
        button.setText("Share contact");
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
            logger.error("Error sending message", e);
        }
    }
}
