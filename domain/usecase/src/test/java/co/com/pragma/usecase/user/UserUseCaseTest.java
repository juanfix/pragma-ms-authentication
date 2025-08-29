package co.com.pragma.usecase.user;

import co.com.pragma.model.role.Role;
import co.com.pragma.model.role.gateways.RoleRepository;
import co.com.pragma.model.user.User;
import co.com.pragma.model.user.gateways.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class UserUseCaseTest {
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private UserUseCase userUseCase;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        roleRepository = mock(RoleRepository.class);
        userUseCase = new UserUseCase(userRepository, roleRepository);
    }

    @Test
    void shouldCreateANewUser() {
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

        StepVerifier.create(userUseCase.saveUser(user))
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

        StepVerifier.create(userUseCase.updateUser(user))
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

        StepVerifier.create(userUseCase.getAllUsers())
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

        StepVerifier.create(userUseCase.getUserByIdentityNumber(identityNumber))
                .expectNext(user)
                .verifyComplete();

        verify(userRepository, times(1)).findByIdentityNumber(identityNumber);
    }

    @Test
    void shouldFailsWhenEmailFormatIsIncorrect() {
        User user = User.builder()
                .id(1L)
                .name("Juan")
                .lastname("Ceballos")
                .email("juanmail.com") // Email no correcto
                .identityNumber("123456")
                .baseSalary(3000000L)
                .build();

        Role role = new Role(1L, "ADMIN", "Administrador");

        when(userRepository.findByIdentityNumber("123456"))
                .thenReturn(Mono.empty());
        when(roleRepository.findById(any()))
                .thenReturn(Mono.just(role));

        StepVerifier.create(userUseCase.saveUser(user))
                .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException&&
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
                .baseSalary(3000000L)
                .build();

        when(userRepository.findByEmail(user.getEmail()))
                .thenReturn(Mono.empty());
        when(roleRepository.findById(any()))
                .thenReturn(Mono.empty());

        StepVerifier.create(userUseCase.saveUser(user))
                .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().equals("El Id del rol suministrado no existe."))
                .verify();

        verify(userRepository, never()).save(user);
    }
}
