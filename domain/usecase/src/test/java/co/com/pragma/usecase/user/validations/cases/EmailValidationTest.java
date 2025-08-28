package co.com.pragma.usecase.user.validations.cases;

import co.com.pragma.model.user.User;
import co.com.pragma.model.user.gateways.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.*;

class EmailValidationTest {

    private UserRepository userRepository;
    private EmailValidation emailValidation;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        emailValidation = new EmailValidation(userRepository);
    }

    @Test
    void shouldFailWhenEmailIsEmpty() {
        User user = User.builder().email("").build();

        StepVerifier.create(emailValidation.validate(user))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException &&
                        e.getMessage().contains("El campo email es obligatorio."))
                .verify();
    }

    @Test
    void shouldFailWhenEmailIsNull() {
        User user = User.builder().email(null).build();

        StepVerifier.create(emailValidation.validate(user))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException &&
                        e.getMessage().contains("El campo email es obligatorio."))
                .verify();
    }

    @Test
    void shouldFailWhenEmailFormatIsInvalid() {
        User user = User.builder().email("hola").build();

        StepVerifier.create(emailValidation.validate(user))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException &&
                        e.getMessage().contains("El campo email debe tener un formato válido."))
                .verify();
    }

    @Test
    void shouldPassWhenEmailDoesNotExistPreviously() {
        User user = User.builder().email("juan@mail.com").build();

        when(userRepository.findByEmail("juan@mail.com"))
                .thenReturn(Mono.empty());

        StepVerifier.create(emailValidation.validate(user))
                .verifyComplete();

        verify(userRepository).findByEmail("juan@mail.com");
    }

    @Test
    void shouldFailWhenEmailExistPreviously() {
        User user = User.builder().email("juan@mail.com").build();

        when(userRepository.findByEmail("juan@mail.com"))
                .thenReturn(Mono.just(user));

        StepVerifier.create(emailValidation.validate(user))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException &&
                        e.getMessage().contains("El email indicado ya existe, intente con otro."))
                .verify();

        verify(userRepository).findByEmail("juan@mail.com");
    }


}
