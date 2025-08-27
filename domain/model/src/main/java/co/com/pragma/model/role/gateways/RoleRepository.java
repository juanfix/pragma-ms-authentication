package co.com.pragma.model.role.gateways;

import co.com.pragma.model.role.Role;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface RoleRepository {
    Mono<Role> saveRole(Role role);
    Flux<Role> getAllRoles();
    Mono<Role> getRoleById(Long id);
    Mono<Role> editRole(Role user);
    Mono<Void> deleteRole();
}
