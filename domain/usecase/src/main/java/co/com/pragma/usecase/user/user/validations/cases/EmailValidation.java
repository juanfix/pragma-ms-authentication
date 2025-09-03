package co.com.pragma.usecase.user.user.validations.cases;

import co.com.pragma.model.user.User;
import co.com.pragma.model.user.gateways.UserRepository;
import co.com.pragma.usecase.user.user.validations.IUserValidation;
import reactor.core.publisher.Mono;

public class EmailValidation implements IUserValidation {
    private final UserRepository userRepository;

    public EmailValidation(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Mono<Void> validate(User user) {
        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            return Mono.error(new IllegalArgumentException("El campo email es obligatorio."));
        }
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        if (!user.getEmail().matches(emailRegex)) {
            return Mono.error(new IllegalArgumentException("El campo email debe tener un formato válido."));
        }
        return userRepository.findByEmail(user.getEmail())
                .flatMap(existing -> Mono.error(new IllegalArgumentException("El email indicado ya existe, intente con otro.")))
                .then();
    }
}
