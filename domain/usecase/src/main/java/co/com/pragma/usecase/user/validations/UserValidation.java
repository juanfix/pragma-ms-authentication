package co.com.pragma.usecase.user.validations;

import co.com.pragma.model.user.User;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

public class UserValidation {
    private final List<IUserValidation> validations = new ArrayList<>();

    public UserValidation includeValidation(IUserValidation validation) {
        validations.add(validation);
        return this;
    }

    public Mono<Void> validate(User user) {
        Mono<Void> result = Mono.empty();
        for (IUserValidation validation : validations) {
            result = result.then(validation.validate(user));
        }
        return result;
    }
}
