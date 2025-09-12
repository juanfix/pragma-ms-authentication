package co.com.pragma.usecase.user.user;

import co.com.pragma.model.user.User;
import co.com.pragma.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class UpdateUserUseCase implements UpdateUserUseCaseInterface {

    private final UserRepository userRepository;

    @Override
    public Mono<User> execute(User user) {
        return userRepository.save(user);
    }
}
