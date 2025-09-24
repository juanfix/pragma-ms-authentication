package co.com.pragma.usecase.user;

import co.com.pragma.model.role.gateways.RoleRepository;
import co.com.pragma.model.user.User;
import co.com.pragma.model.user.gateways.UserRepository;
import co.com.pragma.usecase.user.user.FindUserUseCase;
import co.com.pragma.usecase.user.user.UpdateUserUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class FindUserUseCaseTest {
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private FindUserUseCase findUserUseCase;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        findUserUseCase = new FindUserUseCase(userRepository);
    }

    @Test
    void shouldFindAllUsers() {
        User user1 = User.builder()
                .id(1L)
                .name("Juan")
                .lastname("Ceballos")
                .email("juan@mail.com")
                .identityNumber("123456")
                .baseSalary(3000000L)
                .build();

        User user2 = User.builder()
                .id(1L)
                .name("Leidy")
                .lastname("Pelaez")
                .email("leidy@mail.com")
                .identityNumber("654321")
                .baseSalary(3000000L)
                .build();

        when(userRepository.findAll()).thenReturn(Flux.just(user1, user2));

        StepVerifier.create(findUserUseCase.getAllUsers())
                .expectNext(user1)
                .expectNext(user2)
                .verifyComplete();

        verify(userRepository, times(1)).findAll();

    }

    @Test
    void shouldFindUserByIdentityNumber() {
        String identityNumber = "123";
        User user = User.builder()
                .id(1L)
                .name("Juan")
                .lastname("Ceballos")
                .email("juan@mail.com")
                .identityNumber(identityNumber)
                .baseSalary(3000000L)
                .build();

        when(userRepository.findByIdentityNumber(identityNumber)).thenReturn(Mono.just(user));

        StepVerifier.create(findUserUseCase.getUserByIdentityNumber(identityNumber))
                .expectNext(user)
                .verifyComplete();

        verify(userRepository, times(1)).findByIdentityNumber(identityNumber);
    }
}
