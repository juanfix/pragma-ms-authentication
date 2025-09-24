package co.com.pragma.usecase.user.user.validations.cases;

import co.com.pragma.model.user.User;
import co.com.pragma.usecase.user.user.validations.IUserValidation;
import co.com.pragma.usecase.user.user.validations.error.UserValidationException;
import reactor.core.publisher.Mono;

public class NameValidation implements IUserValidation {
    @Override
    public Mono<Void> validate(User user) {
        if (user.getName() == null || user.getName().isEmpty()) {
            return Mono.error(new UserValidationException("El campo nombre es obligatorio."));
        }
        return Mono.empty();
    }
}
