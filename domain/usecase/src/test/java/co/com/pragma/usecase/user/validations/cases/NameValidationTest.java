package co.com.pragma.usecase.user.validations.cases;

import co.com.pragma.model.user.User;
import co.com.pragma.usecase.user.user.validations.cases.NameValidation;
import co.com.pragma.usecase.user.user.validations.error.UserValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

class NameValidationTest {

    private NameValidation nameValidation;

    @BeforeEach
    void setUp() {
        nameValidation = new NameValidation();
    }

    @Test
    void shouldPassWhenNameIsNotEmpty() {
        User user = User.builder().name("Juan").build();

        StepVerifier.create(nameValidation.validate(user))
                .verifyComplete();
    }

    @Test
    void shouldFailsWhenNameIsEmpty() {
        User user = User.builder().name(null).build();

        StepVerifier.create(nameValidation.validate(user))
                .expectErrorMatches(e -> e instanceof UserValidationException &&
                        e.getMessage().contains("El campo nombre es obligatorio."))
                .verify();
    }

}
