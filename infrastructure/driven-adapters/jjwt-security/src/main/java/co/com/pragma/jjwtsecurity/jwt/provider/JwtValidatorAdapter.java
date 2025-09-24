package co.com.pragma.jjwtsecurity.jwt.provider;

import co.com.pragma.usecase.user.jwt.IJwtValidation;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class JwtValidatorAdapter implements IJwtValidation {

    private final JwtProvider jwtProvider;
    private static final Logger log = LoggerFactory.getLogger(JwtValidatorAdapter.class);

    @Override
    public Mono<Boolean> validate(String jwt) {
        return Mono.justOrEmpty(jwt)
                .filter(token -> token.startsWith("Bearer "))
                .map(token -> token.substring(7))
                .doOnNext(t -> log.debug("Se extrae el token: {}", t))
                .map(jwtProvider::validateToken)
                .doOnNext(valid -> {
                    log.error("JWT no válido");
                })
                .defaultIfEmpty(Mono.just(false))
                .doOnError(e -> log.error("Ha ocurrido un error al validar el Token: {}", e.getMessage()))
                .flatMap(mono -> mono);
    }
}
