package co.com.pragma.usecase.user.validations;

import co.com.pragma.model.user.User;
import co.com.pragma.usecase.user.user.validations.IUserValidation;
import co.com.pragma.usecase.user.user.validations.UserValidation;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.*;

class UserValidationTest {

    @Test
    void shouldBeCalled() {
        IUserValidation validation = mock(IUserValidation.class);

        when(validation.validate(any())).thenReturn(Mono.empty());

        UserValidation userValidation = new UserValidation()
                .includeValidation(validation);

        StepVerifier.create(userValidation.validate(new User()))
                .verifyComplete();

        verify(validation).validate(any());
    }

    @Test
    void shouldFailWhenAtLeastOneValidationReturnsAnException() {
        IUserValidation validationSuccess = mock(IUserValidation.class);
        IUserValidation validationFail = mock(IUserValidation.class);

        when(validationSuccess.validate(any())).thenReturn(Mono.empty());
        when(validationFail.validate(any())).thenReturn(Mono.error(new IllegalArgumentException("Any error message")));

        UserValidation userValidation = new UserValidation()
                .includeValidation(validationSuccess)
                .includeValidation(validationFail);

        StepVerifier.create(userValidation.validate(new User()))
                .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException&&
                        throwable.getMessage().equals("Any error message"))
                .verify();

        verify(validationSuccess).validate(any());
        verify(validationFail).validate(any());
    }
}
