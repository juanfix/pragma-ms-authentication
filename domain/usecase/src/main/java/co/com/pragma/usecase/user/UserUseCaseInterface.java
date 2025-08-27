package co.com.pragma.usecase.user;

import co.com.pragma.model.user.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserUseCaseInterface {
    public Mono<User> saveUser(User user);
    public Mono<User> updateUser(User user);
    public Flux<User> getAllUsers();
    public Mono<User> getUserByIdentityNumber(String identityNumber);
    public Mono<Void> deleteUser(Long id);
}
