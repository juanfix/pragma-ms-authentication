package co.com.pragma.usecase.user.user;

import co.com.pragma.model.user.dto.LoginDTO;
import co.com.pragma.model.user.dto.TokenDTO;
import reactor.core.publisher.Mono;

public interface LoginUseCaseInterface {
    public Mono<TokenDTO> login(LoginDTO dto);
}
