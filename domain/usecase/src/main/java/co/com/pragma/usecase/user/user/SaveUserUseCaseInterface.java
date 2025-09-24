package co.com.pragma.usecase.user.user;

import co.com.pragma.model.user.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface SaveUserUseCaseInterface {
    public Mono<User> execute(User user);
}
