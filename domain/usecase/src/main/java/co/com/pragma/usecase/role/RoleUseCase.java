package co.com.pragma.usecase.role;

import co.com.pragma.model.role.Role;
import co.com.pragma.model.role.gateways.RoleRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class RoleUseCase implements RoleUseCaseInterface {
    private final RoleRepository roleRepository;
    @Override
    public Mono<Role> getRoleById(Long id) {
        return roleRepository.findById(id);
    }
}
