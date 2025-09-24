package co.com.pragma.usecase.role;

import co.com.pragma.model.role.Role;
import reactor.core.publisher.Mono;

public interface RoleUseCaseInterface {
    public Mono<Role> getRoleById(Long id);
}
