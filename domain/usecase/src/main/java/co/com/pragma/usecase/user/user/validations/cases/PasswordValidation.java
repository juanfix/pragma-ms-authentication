package co.com.pragma.usecase.user.user.validations.cases;

import co.com.pragma.model.user.User;
import co.com.pragma.usecase.user.user.validations.IUserValidation;
import reactor.core.publisher.Mono;

public class PasswordValidation implements IUserValidation {


    @Override
    public Mono<Void> validate(User user) {
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            return Mono.error(new IllegalArgumentException("El campo contraseña es obligatorio."));
        }
        return Mono.empty();
    }
}
