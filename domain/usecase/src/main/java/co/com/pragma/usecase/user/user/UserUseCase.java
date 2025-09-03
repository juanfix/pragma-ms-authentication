package co.com.pragma.usecase.user.user;

import co.com.pragma.model.role.gateways.RoleRepository;
import co.com.pragma.model.user.User;
import co.com.pragma.model.user.gateways.UserRepository;
import co.com.pragma.usecase.user.user.validations.UserValidation;
import co.com.pragma.usecase.user.user.validations.cases.BaseSalaryValidation;
import co.com.pragma.usecase.user.user.validations.cases.EmailValidation;
import co.com.pragma.usecase.user.user.validations.cases.LastnameValidation;
import co.com.pragma.usecase.user.user.validations.cases.NameValidation;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class UserUseCase implements UserUseCaseInterface {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public Mono<User> saveUser(User user) {
        UserValidation userValidation = new UserValidation()
                .includeValidation(new NameValidation())
                .includeValidation(new LastnameValidation())
                .includeValidation(new EmailValidation(userRepository))
                .includeValidation(new BaseSalaryValidation());

        return userValidation.validate(user)
                .then(roleRepository.findById(user.getRoleId())
                        .switchIfEmpty(Mono.error(new IllegalArgumentException("El Id del rol suministrado no existe.")))
                        .flatMap(role -> {
                            User userValidated = user;
                            return userRepository.save(userValidated);
                        }).log()
                );
    }

    public Mono<User> updateUser(User user) {
        return userRepository.save(user);
    }

    public Flux<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Mono<User> getUserByIdentityNumber(String identityNumber) {
        return userRepository.findByIdentityNumber(identityNumber);
    }

    public Mono<Void> deleteUser(Long id) {
        return userRepository.deleteById(id);
    }

}
