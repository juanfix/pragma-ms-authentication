package co.com.pragma.model.user.gateways;

import co.com.pragma.model.user.dto.LoginDTO;
import co.com.pragma.model.user.dto.TokenDTO;
import reactor.core.publisher.Mono;

public interface AuthRepository {
    Mono<TokenDTO> login(LoginDTO dto);
}
