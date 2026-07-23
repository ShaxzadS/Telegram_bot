package Bot.ShaxzadBot.service;

import Bot.ShaxzadBot.entity.Atm;
import Bot.ShaxzadBot.repository.AtmRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Component
@Profile("dev")
public class AtmDataSeeder implements ApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(AtmDataSeeder.class);

    private final AtmRepository atmRepository;

    public AtmDataSeeder(AtmRepository atmRepository) {
        this.atmRepository = atmRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (atmRepository.count() > 0) {
            logger.info("ATM table already contains data, skipping seed");
            return;
        }

        List<Atm> items = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                new ClassPathResource("atm_data.csv").getInputStream(),
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

                items.add(new Atm(
                        number,
                        columns[1].trim(),
                        columns[2].trim(),
                        columns[3].trim(),
                        columns[4].trim()
                ));
            }

            atmRepository.saveAll(items);
            logger.info("Seeded ATM rows: {}", items.size());
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load atm_data.csv", e);
        }
    }
}
