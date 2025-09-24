package co.com.pragma.usecase.user.validations.cases;

import co.com.pragma.model.user.User;
import co.com.pragma.usecase.user.user.validations.cases.BaseSalaryValidation;
import co.com.pragma.usecase.user.user.validations.error.UserValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

class BaseSalaryValidationTest {

    private BaseSalaryValidation baseSalaryValidation;

    @BeforeEach
    void setUp() {
        baseSalaryValidation = new BaseSalaryValidation();
    }

    @Test
    void shouldPassWhenBaseSalaryIsBetween0And15000000() {
        User user = User.builder().baseSalary(5000000L).build();

        StepVerifier.create(baseSalaryValidation.validate(user))
                .verifyComplete();
    }

    @Test
    void shouldFailWhenBaseSalaryIsNotBetween0And15000000() {
        User user = User.builder().baseSalary(-25L).build();

        StepVerifier.create(baseSalaryValidation.validate(user))
                .expectErrorMatches(e -> e instanceof UserValidationException &&
                        e.getMessage().contains("El campo salario base debe ser un numero entre 0 y 15000000."))
                .verify();
    }

    @Test
    void shouldFailsWhenBaseSalaryIsEmpty() {
        User user = User.builder().baseSalary(null).build();

        StepVerifier.create(baseSalaryValidation.validate(user))
                .expectErrorMatches(e -> e instanceof UserValidationException &&
                        e.getMessage().contains("El campo salario base es requerido."))
                .verify();
    }

}
