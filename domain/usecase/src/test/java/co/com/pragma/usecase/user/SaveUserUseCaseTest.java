package co.com.pragma.usecase.user;

import co.com.pragma.model.role.Role;
import co.com.pragma.model.role.gateways.RoleRepository;
import co.com.pragma.model.user.User;
import co.com.pragma.model.user.gateways.UserRepository;
import co.com.pragma.usecase.user.user.SaveUserUseCase;
import co.com.pragma.usecase.user.user.validations.error.UserValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class SaveUserUseCaseTest {
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private SaveUserUseCase saveUserUseCase;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        roleRepository = mock(RoleRepository.class);
        saveUserUseCase = new SaveUserUseCase(userRepository, roleRepository);
    }

    @Test
    void shouldCreateANewUser() {
        User user = User.builder()
                .id(1L)
                .name("Juan")
                .lastname("Ceballos")
                .email("juan@mail.com")
                .identityNumber("123456")
                .password("123456")
                .baseSalary(3000000L)
                .build();

        Role role = new Role(1L, "ADMIN", "Administrador");

        when(userRepository.findByEmail("juan@mail.com"))
                .thenReturn(Mono.empty());
        when(roleRepository.findById(any()))
                .thenReturn(Mono.just(role));
        when(userRepository.save(user))
                .thenAnswer(invocation -> {
                    User u = invocation.getArgument(0);
                    u.setRoleId(role.getId());
                    return Mono.just(u);
                });

        StepVerifier.create(saveUserUseCase.execute(user))
                .expectNextMatches(u -> u.getName().equals("Juan") &&
                        u.getRoleId() != null &&
                        u.getRoleId().equals(1L))
                .verifyComplete();

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User savedUser = captor.getValue();
        assertEquals("Juan", savedUser.getName());
        assertEquals(1L, savedUser.getRoleId());
    }

    @Test
    void shouldFailsWhenEmailFormatIsIncorrect() {
        User user = User.builder()
                .id(1L)
                .name("Juan")
                .lastname("Ceballos")
                .email("juanmail.com") // Email no correcto
                .identityNumber("123456")
                .password("123456")
                .baseSalary(3000000L)
                .build();

        Role role = new Role(1L, "ADMIN", "Administrador");

        when(userRepository.findByIdentityNumber("123456"))
                .thenReturn(Mono.empty());
        when(roleRepository.findById(any()))
                .thenReturn(Mono.just(role));

        StepVerifier.create(saveUserUseCase.execute(user))
                .expectErrorMatches(throwable -> throwable instanceof UserValidationException &&
                        throwable.getMessage().equals("El campo email debe tener un formato válido."))
                .verify();

        verify(userRepository, never()).save(user);
    }

    @Test
    void shouldFailsWhenRoleDoesNotExist() {
        User user = User.builder()
                .id(1L)
                .name("Juan")
                .lastname("Ceballos")
                .email("juan@mail.com")
                .identityNumber("123456")
                .password("123456")
                .baseSalary(3000000L)
                .build();

        when(userRepository.findByEmail(user.getEmail()))
                .thenReturn(Mono.empty());
        when(roleRepository.findById(any()))
                .thenReturn(Mono.empty());

        StepVerifier.create(saveUserUseCase.execute(user))
                .expectErrorMatches(throwable -> throwable instanceof UserValidationException &&
                        throwable.getMessage().equals("El Id del rol suministrado no existe."))
                .verify();

        verify(userRepository, never()).save(user);
    }
}
