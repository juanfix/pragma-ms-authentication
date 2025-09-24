package co.com.pragma.usecase.user.user;

import co.com.pragma.model.user.dto.LoginDTO;
import co.com.pragma.model.user.dto.TokenDTO;
import co.com.pragma.model.user.gateways.AuthRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class LoginUseCase implements LoginUseCaseInterface {
    private final AuthRepository authRepository;

    public Mono<TokenDTO> login(LoginDTO dto) {
        return authRepository.login(dto);
    }
}
