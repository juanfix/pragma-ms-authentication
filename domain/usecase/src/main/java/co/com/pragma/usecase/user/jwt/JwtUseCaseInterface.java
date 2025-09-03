package co.com.pragma.usecase.user.jwt;

import reactor.core.publisher.Mono;

public interface JwtUseCaseInterface {
    Mono<Boolean> isValidUser(String jwt, String identityNumber, String email);
}
