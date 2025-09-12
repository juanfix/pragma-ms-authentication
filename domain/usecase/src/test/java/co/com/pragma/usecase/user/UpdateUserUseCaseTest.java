package co.com.pragma.usecase.user;

import co.com.pragma.model.role.Role;
import co.com.pragma.model.role.gateways.RoleRepository;
import co.com.pragma.model.user.User;
import co.com.pragma.model.user.gateways.UserRepository;
import co.com.pragma.usecase.user.user.UpdateUserUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.mock;

class UpdateUserUseCaseTest {

    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private UpdateUserUseCase updateUserUseCase;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        roleRepository = mock(RoleRepository.class);
        updateUserUseCase = new UpdateUserUseCase(userRepository);
    }

    @Test
    void shouldEditAnUser() {
        User user = User.builder()
                .id(1L)
                .name("Juan")
                .lastname("Ceballos")
                .email("juan@mail.com")
                .identityNumber("123456")
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

        user.setName("Juanjo");

        StepVerifier.create(updateUserUseCase.execute(user))
                .expectNextMatches(u -> u.getName().equals("Juanjo") &&
                        u.getRoleId() != null &&
                        u.getRoleId().equals(1L))
                .verifyComplete();

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User savedUser = captor.getValue();
        assertEquals("Juanjo", savedUser.getName());
        assertEquals(1L, savedUser.getRoleId());
    }
}
