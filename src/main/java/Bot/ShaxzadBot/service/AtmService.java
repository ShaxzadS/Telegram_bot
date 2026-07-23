package Bot.ShaxzadBot.service;

import Bot.ShaxzadBot.entity.Atm;
import Bot.ShaxzadBot.repository.AtmRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AtmService {

    private final AtmRepository atmRepository;

    public AtmService(AtmRepository atmRepository) {
        this.atmRepository = atmRepository;
    }

    public Optional<Atm> findByNumber(String number) {
        return atmRepository.findById(number);
    }
}
