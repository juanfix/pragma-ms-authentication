package co.com.pragma.model.user.gateways;

import co.com.pragma.model.user.User;
import co.com.pragma.model.user.dto.UserMailByRoleDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserRepository {
    Mono<User> save(User user);
    Flux<User> findAll();
    Flux<UserMailByRoleDTO> getAllUsersMailByRole(Long id);
    Mono<User> findByIdentityNumber(String identityNumber);
    Mono<User> findByEmail(String email);
    Mono<User> findByIdentityNumberAndEmail(String identityNumber, String email);
}
