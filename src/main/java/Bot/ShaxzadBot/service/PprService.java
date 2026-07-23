package Bot.ShaxzadBot.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PprService {

    private final Map<String, List<String>> pprKit = new HashMap<>();

    public PprService() {
        List<String> baseKit = List.of(
                "Key",
                "Master key",
                "Tools",
                "Special tools",
                "Blower",
                "Flash drive",
                "Consumables"
        );

        pprKit.put("NCR-6634 cash-in", addExtra(baseKit, "Cassettes", "Banknotes"));
        pprKit.put("Diebold Nixdorf C4060", addExtra(baseKit, "Diebold service key"));
        pprKit.put("GRG H68VL", addExtra(baseKit, "GRG key"));
        pprKit.put("APP-2", addExtra(baseKit, "APP technical key"));
    }

    private List<String> addExtra(List<String> base, String... extras) {
        List<String> result = new ArrayList<>(base);
        result.addAll(List.of(extras));
        return result;
    }

    public List<String> getKitByModel(String model) {
        return pprKit.getOrDefault(model, List.of("No PPR data for this model"));
    }
}