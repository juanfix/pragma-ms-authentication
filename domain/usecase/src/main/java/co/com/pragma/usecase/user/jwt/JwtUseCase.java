package co.com.pragma.usecase.user.jwt;

import co.com.pragma.model.user.gateways.UserRepository;
import co.com.pragma.usecase.user.user.validations.error.UserValidationException;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class JwtUseCase implements JwtUseCaseInterface {

    private final UserRepository userRepository;
    private final IJwtValidation iJwtValidation;

    @Override
    public Mono<Boolean> isValidUser(String jwt, String identityNumber, String email) {
        return iJwtValidation.validate(jwt)
                .flatMap(isValid -> {
                    if (Boolean.TRUE.equals(isValid)) {
                        return userRepository.findByIdentityNumberAndEmail(identityNumber, email)
                                .flatMap(user ->
                                        {
                                            return Mono.just(user.getIdentityNumber().equalsIgnoreCase(identityNumber));
                                        }
                                ).defaultIfEmpty(false);
                    } else {
                        return Mono.error(new UserValidationException("Invalid token"));
                    }
                });
    }
}
