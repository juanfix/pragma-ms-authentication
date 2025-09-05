package co.com.pragma.r2dbc;

import co.com.pragma.jjwtsecurity.jwt.provider.JwtProvider;
import co.com.pragma.model.user.User;
import co.com.pragma.model.user.dto.LoginDTO;
import co.com.pragma.model.user.dto.TokenDTO;
import co.com.pragma.model.user.gateways.AuthRepository;
import co.com.pragma.model.user.gateways.UserRepository;
import co.com.pragma.r2dbc.entity.UserEntity;
import co.com.pragma.r2dbc.helper.ReactiveAdapterOperations;
import co.com.pragma.usecase.user.user.validations.error.UserValidationException;
import org.reactivecommons.utils.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class UserRepositoryAdapter extends ReactiveAdapterOperations<
    User/* change for domain model */,
    UserEntity/* change for adapter model */,
    Long,
    UserReactiveRepository
> implements UserRepository, AuthRepository {
    private static final Logger log = LoggerFactory.getLogger(UserRepositoryAdapter.class);
    private final TransactionalOperator transactionalOperator;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final RoleRepositoryAdapter roleRepositoryAdapter;

    public UserRepositoryAdapter(UserReactiveRepository repository,
                                 ObjectMapper mapper,
                                 TransactionalOperator transactionalOperator,
                                 PasswordEncoder passwordEncoder,
                                 JwtProvider jwtProvider,
                                 RoleRepositoryAdapter roleRepositoryAdapter
    ) {
        /**
         *  Could be use mapper.mapBuilder if your domain model implement builder pattern
         *  super(repository, mapper, d -> mapper.mapBuilder(d,ObjectModel.ObjectModelBuilder.class).build());
         *  Or using mapper.map with the class of the object model
         */
        super(repository, mapper, d -> mapper.map(d, User.class));
        this.transactionalOperator = transactionalOperator;
        this.passwordEncoder = passwordEncoder;
        this.jwtProvider = jwtProvider;
        this.roleRepositoryAdapter = roleRepositoryAdapter;
    }

    @Override
    @Transactional
    public Mono<User> save(User user) {
        String encryptedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encryptedPassword);
        return transactionalOperator.execute(status ->
                super.save(user)).next();
    }

    @Override
    @Transactional
    public Flux<User> findAll() {
        return transactionalOperator.execute(status -> super.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<User> findByIdentityNumber(String identityNumber) {
        User user = new User();
        user.setIdentityNumber(identityNumber);

        return transactionalOperator.execute(status -> findByExample(user)
                .switchIfEmpty(Mono.empty())).next();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<User> findByEmail(String email) {
        User user = new User();
        user.setEmail(email);

        return transactionalOperator.execute(status -> findByExample(user)
                .switchIfEmpty(Mono.empty())).next();
    }

    @Override
    public Mono<User> findByIdentityNumberAndEmail(String identityNumber, String email) {
        log.info("Searching user by email={} and identity number={}", email, identityNumber);

        User user = new User();
        user.setIdentityNumber(identityNumber);
        user.setEmail(email);

        return transactionalOperator.execute(status -> findByExample(user)
                .switchIfEmpty(Mono.defer(() -> {
                    log.warn("User with email={} and identity number={} not found", identityNumber, email);
                    return Mono.empty();
                }))).next();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<User> findById(Long id) {
        return transactionalOperator.execute(status -> super.findById(id)).next();
    }

    @Override
    public Mono<TokenDTO> login(LoginDTO dto) {
        User user = new User();
        user.setEmail(dto.email());

        return transactionalOperator.execute(status -> findByExample(user)
                .filter(userDocument -> passwordEncoder.matches(dto.password(), userDocument.getPassword()))
                .flatMap(userDocument ->
                    roleRepositoryAdapter.findById(userDocument.getRoleId())
                            .map(role -> {
                                String roleName = role.getName();
                                return new TokenDTO(jwtProvider.generateToken(userDocument, roleName));
                            })
                )
                .switchIfEmpty(Mono.error(new UserValidationException("Bad credentials")))).next();
    }
}
