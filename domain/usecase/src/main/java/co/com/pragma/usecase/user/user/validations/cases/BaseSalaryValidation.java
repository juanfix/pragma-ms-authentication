package co.com.pragma.usecase.user.user.validations.cases;

import co.com.pragma.model.user.User;
import co.com.pragma.usecase.user.user.validations.IUserValidation;
import co.com.pragma.usecase.user.user.validations.error.UserValidationException;
import reactor.core.publisher.Mono;

public class BaseSalaryValidation implements IUserValidation {
    @Override
    public Mono<Void> validate(User user) {
        if (user.getBaseSalary() == null) {
            return Mono.error(new UserValidationException("El campo salario base es requerido."));
        }
        if (user.getBaseSalary() < 0 || user.getBaseSalary() > 15000000) {
            return Mono.error(new UserValidationException("El campo salario base debe ser un numero entre 0 y 15000000."));
        }
        return Mono.empty();
    }
}
