package Bot.ShaxzadBot.service;

import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PprService {

    private final Map<String, List<String>> pprKit = new HashMap<>();

    public PprService() {

        List<String> baseKit = List.of(
                "🔑 Ключ",
                "🔐 Мастерключ",
                "🧰 Инструменты",
                "🛠 Спец инструменты",
                "💨 Пыледув",
                "💾 Флэшка",
                "📦 Расходники"
        );

        pprKit.put("NCR-6634 cash-in",
                addExtra(baseKit, "📦 Кассеты", "💵 Купюры"));

        pprKit.put("Diebold Nixdorf C4060",
                addExtra(baseKit, "🔑 Сервисный ключ Diebold"));

        pprKit.put("GRG H68VL",
                addExtra(baseKit, "🔑 GRG ключ"));

        pprKit.put("АПП-2",
                addExtra(baseKit, "🔑 Техключ АПП"));
    }

    private List<String> addExtra(List<String> base, String... extras) {
        List<String> result = new ArrayList<>(base);
        result.addAll(List.of(extras));
        return result;
    }

    public List<String> getKitByModel(String model) {
        return pprKit.getOrDefault(model,
                List.of("⚠ Нет данных по ППР для этой модели"));
    }
}