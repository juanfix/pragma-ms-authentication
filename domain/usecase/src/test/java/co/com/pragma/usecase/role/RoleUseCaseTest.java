package co.com.pragma.usecase.role;

import co.com.pragma.model.role.Role;
import co.com.pragma.model.role.gateways.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import static org.mockito.Mockito.mock;

class RoleUseCaseTest {
    private RoleRepository roleRepository;
    private RoleUseCase roleUseCase;

    @BeforeEach
    void setUp() {
        roleRepository = mock(RoleRepository.class);
        roleUseCase = new RoleUseCase(roleRepository);
    }

    @Test
    void shouldFindRoleById() {
        Long id = 1L;
        Role role = Role.builder()
                .id(1L)
                .name("ADMIN")
                .description("Admin")
                .build();

        when(roleRepository.findById(id)).thenReturn(Mono.just(role));

        StepVerifier.create(roleUseCase.getRoleById(id))
                .expectNext(role)
                .verifyComplete();

        verify(roleRepository, times(1)).findById(id);
    }

}
