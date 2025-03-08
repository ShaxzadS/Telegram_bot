package Bot.ShaxzadBot.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
@Configuration
@PropertySource("classpath:application.properties")
public class BotConfig {

    @Value("${bot.name}")
    private String botName;

    @Value("${bot.key}")
    private String token;


    public String getBotName() {
        return botName;
    }

    public String getToken() {
        return token;
    }
}

