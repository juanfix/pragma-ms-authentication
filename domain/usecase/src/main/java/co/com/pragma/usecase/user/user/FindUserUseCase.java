package co.com.pragma.usecase.user.user;

import co.com.pragma.model.user.User;
import co.com.pragma.model.user.gateways.UserRepository;
import co.com.pragma.model.user.dto.UserMailByRoleDTO;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class FindUserUseCase implements FindUserUseCaseInterface {

    private final UserRepository userRepository;

    @Override
    public Flux<User> getAllUsers() {
        return userRepository.findAll();
    }


    @Override
    public Flux<UserMailByRoleDTO> getAllUsersMailByRole(Long id) {
        return userRepository.getAllUsersMailByRole(id);
    }

    @Override
    public Mono<User> getUserByIdentityNumber(String identityNumber) {
        return userRepository.findByIdentityNumber(identityNumber);
    }
}
