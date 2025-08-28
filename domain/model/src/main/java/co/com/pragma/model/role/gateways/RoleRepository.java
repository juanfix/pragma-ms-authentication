package co.com.pragma.model.role.gateways;

import co.com.pragma.model.role.Role;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface RoleRepository {
    Mono<Role> save(Role role);
    Flux<Role> findAll();
    Mono<Role> findById(Long id);
    Mono<Role> edit(Role user);
    Mono<Void> deleteById();
}
