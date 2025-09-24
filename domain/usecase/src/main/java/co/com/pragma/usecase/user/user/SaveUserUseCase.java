package co.com.pragma.usecase.user.user;

import co.com.pragma.model.role.gateways.RoleRepository;
import co.com.pragma.model.user.User;
import co.com.pragma.model.user.gateways.UserRepository;
import co.com.pragma.usecase.user.user.validations.UserValidation;
import co.com.pragma.usecase.user.user.validations.cases.*;
import co.com.pragma.usecase.user.user.validations.error.UserValidationException;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class SaveUserUseCase implements SaveUserUseCaseInterface {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Override
    public Mono<User> execute(User user) {
        UserValidation userValidation = new UserValidation()
                .includeValidation(new NameValidation())
                .includeValidation(new LastnameValidation())
                .includeValidation(new PasswordValidation())
                .includeValidation(new EmailValidation(userRepository))
                .includeValidation(new BaseSalaryValidation());

        return userValidation.validate(user)
                .then(roleRepository.findById(user.getRoleId())
                        .switchIfEmpty(Mono.error(new UserValidationException("El Id del rol suministrado no existe.")))
                        .flatMap(role -> {
                            User userValidated = user;
                            return userRepository.save(userValidated);
                        }).log()
                );
    }

}
