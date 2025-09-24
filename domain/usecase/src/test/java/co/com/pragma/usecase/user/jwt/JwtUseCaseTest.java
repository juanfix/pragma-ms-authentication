package co.com.pragma.usecase.user.jwt;

import co.com.pragma.model.user.User;
import co.com.pragma.model.user.gateways.UserRepository;
import co.com.pragma.usecase.user.user.validations.error.UserValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.*;

class JwtUseCaseTest {
    private UserRepository userRepository;
    private IJwtValidation iJwtValidation;
    private JwtUseCase jwtUseCase;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        iJwtValidation = mock(IJwtValidation.class);
        jwtUseCase = new JwtUseCase(userRepository, iJwtValidation);
    }

    @Test
    void shouldReturnTrueWhenIsAValidUser() {
        String jwt = "validJwt";
        String email = "test@email.com";
        String identityNumber = "123";

        User user = new User();
        user.setIdentityNumber("123");

        when(iJwtValidation.validate(jwt)).thenReturn(Mono.just(true));
        when(userRepository.findByIdentityNumberAndEmail(identityNumber, email)).thenReturn(Mono.just(user));

        StepVerifier.create(jwtUseCase.isValidUser(jwt, identityNumber, email))
                .expectNext(true)
                .verifyComplete();

        verify(iJwtValidation).validate(jwt);
        verify(userRepository).findByIdentityNumberAndEmail(identityNumber, email);
    }

    @Test
    void shouldReturnFalseWhenUserIdentityNumberIdDifferent() {
        String jwt = "validJwt";
        String email = "test@email.com";
        String identityNumber = "123";

        User user = new User();
        user.setIdentityNumber("321");

        when(iJwtValidation.validate(jwt)).thenReturn(Mono.just(true));
        when(userRepository.findByIdentityNumberAndEmail(identityNumber, email)).thenReturn(Mono.just(user));

        StepVerifier.create(jwtUseCase.isValidUser(jwt, identityNumber, email))
                .expectNext(false)
                .verifyComplete();
    }

    @Test
    void shouldReturnFalseWhenUserNotFound() {
        String jwt = "validJwt";
        String email = "test@email.com";
        String identityNumber = "123";

        when(iJwtValidation.validate(jwt)).thenReturn(Mono.just(true));
        when(userRepository.findByIdentityNumberAndEmail(identityNumber, email)).thenReturn(Mono.empty());

        StepVerifier.create(jwtUseCase.isValidUser(jwt, identityNumber, email))
                .expectNext(false)
                .verifyComplete();
    }

    @Test
    void shouldThrowErrorWhenJwtInvalid() {
        String jwt = "invalidJwt";
        String email = "test@email.com";
        String document = "123";

        when(iJwtValidation.validate(jwt)).thenReturn(Mono.just(false));

        StepVerifier.create(jwtUseCase.isValidUser(jwt, document, email))
                .expectErrorMatches(throwable -> throwable instanceof UserValidationException &&
                        throwable.getMessage().equals("Invalid token"))
                .verify();
    }



}
