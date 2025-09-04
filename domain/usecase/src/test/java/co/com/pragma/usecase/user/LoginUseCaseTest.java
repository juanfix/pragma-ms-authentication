package co.com.pragma.usecase.user;

import co.com.pragma.model.user.dto.LoginDTO;
import co.com.pragma.model.user.dto.TokenDTO;
import co.com.pragma.model.user.gateways.AuthRepository;
import co.com.pragma.usecase.user.user.LoginUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.*;

class LoginUseCaseTest {
    private AuthRepository authRepository;
    private LoginUseCase loginUseCase;

    @BeforeEach
    void setUp() {
        authRepository = mock(AuthRepository.class);
        loginUseCase = new LoginUseCase(authRepository);
    }


    @Test
    void shouldLoginAnUser() {
        LoginDTO loginDTO = new LoginDTO("juan@mail.com", "123456");

        TokenDTO tokenDTO = new TokenDTO("ey5...");

        when(authRepository.login(loginDTO)).thenReturn(Mono.just(tokenDTO));

        StepVerifier.create(loginUseCase.login(loginDTO))
                .expectNext(tokenDTO)
                .verifyComplete();

        verify(authRepository, times(1)).login(loginDTO);

    }

}
