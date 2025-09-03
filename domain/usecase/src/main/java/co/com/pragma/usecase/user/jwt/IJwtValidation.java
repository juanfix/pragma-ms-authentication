package co.com.pragma.usecase.user.jwt;

import reactor.core.publisher.Mono;

public interface IJwtValidation {
    Mono<Boolean> validate(String jwt);
}
