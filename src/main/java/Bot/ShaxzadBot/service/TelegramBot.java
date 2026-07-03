package Bot.ShaxzadBot.service;

import Bot.ShaxzadBot.config.BotConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import jakarta.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class TelegramBot extends TelegramLongPollingBot {
    private final PprService pprService;
    private static final Logger logger =
            LoggerFactory.getLogger(TelegramBot.class);

    private final BotConfig config;

    //private final Map<String, String> atmDatabase = new HashMap<>();
    private final Map<String, String[]> atmDatabase = new HashMap<>();

    public TelegramBot(BotConfig config, PprService pprService) {
        this.config = config;
        this.pprService = pprService;
    }

    // 🔥 Загружаем CSV при старте
    @PostConstruct
    public void loadAtmData() {

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        getClass().getClassLoader()
                                .getResourceAsStream("atm_data.csv"),
                        StandardCharsets.UTF_8))) {

            String line;
            int lineNumber = 0;

            while ((line = reader.readLine()) != null) {
                lineNumber++;

                String[] columns = line.split(";", -1);

                if (columns.length < 5) {
                    logger.warn("Skipping invalid ATM CSV row {}: expected 5 columns, got {}", lineNumber, columns.length);
                    continue;
                }

                String number = columns[0].trim();
                if (!number.matches("\\d+")) {
                    logger.warn("Skipping invalid ATM CSV row {}: ATM number is not numeric", lineNumber);
                    continue;
                }

                String model = columns[1].trim();
                String org = columns[2].trim();
                String address = columns[3].trim();
                String sector = columns[4].trim();

                String fullInfo =
                        "🏧 №АТМ: " + number + "\n" +
                                "📟 Модель: " + model + "\n" +
                                "🏢 Организация: " + org + "\n" +
                                "📍 Адрес: " + address + "\n" +
                                "🗂 " + sector;

                atmDatabase.put(number, new String[]{model, fullInfo});
            }

            logger.info("Loaded ATM count: {}", atmDatabase.size());

        } catch (Exception e) {
            logger.error("CSV load error", e);
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

    @Override
    public void onUpdateReceived(Update update) {

        if (update.hasMessage() && update.getMessage().hasText()) {

            String chatId = update.getMessage().getChatId().toString();
            String text = update.getMessage().getText().trim();

            String response;

            if (text.matches("\\d+")) {

                if (atmDatabase.containsKey(text)) {

                    String model = atmDatabase.get(text)[0];
                    String info = atmDatabase.get(text)[1];

                    List<String> kit = pprService.getKitByModel(model);

                    StringBuilder pprText = new StringBuilder("\n\n🧰 Комплект для ППР:\n\n");

                    for (String item : kit) {
                        pprText.append(item).append("\n");
                    }

                    response = info + pprText.toString();

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
                logger.error("Ошибка отправки", e);
            }
        }
    }
}
