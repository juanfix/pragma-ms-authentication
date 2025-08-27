package co.com.pragma.model.user.gateways;

import co.com.pragma.model.user.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserRepository {
    Mono<User> save(User user);
    Flux<User> findAll();
    Mono<User> findByIdentityNumber(String identityNumber);
    Mono<User> findByEmail(String email);
    Mono<Void> deleteById(Long id);
}
