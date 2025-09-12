package co.com.pragma.usecase.user.user;

import co.com.pragma.model.user.User;
import co.com.pragma.model.user.gateways.UserRepository;
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
    public Mono<User> getUserByIdentityNumber(String identityNumber) {
        return userRepository.findByIdentityNumber(identityNumber);
    }
}
