package co.com.pragma.r2dbc;

import co.com.pragma.model.user.User;
import co.com.pragma.model.user.gateways.UserRepository;
import co.com.pragma.r2dbc.entity.UserEntity;
import co.com.pragma.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
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
> implements UserRepository {

    private final TransactionalOperator transactionalOperator;

    public UserRepositoryAdapter(UserReactiveRepository repository, ObjectMapper mapper, TransactionalOperator transactionalOperator) {
        /**
         *  Could be use mapper.mapBuilder if your domain model implement builder pattern
         *  super(repository, mapper, d -> mapper.mapBuilder(d,ObjectModel.ObjectModelBuilder.class).build());
         *  Or using mapper.map with the class of the object model
         */
        super(repository, mapper, d -> mapper.map(d, User.class));
        this.transactionalOperator = transactionalOperator;
    }

    @Override
    @Transactional
    public Mono<User> save(User user) {
        return transactionalOperator.execute(status -> super.save(user)).next();
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
    @Transactional(readOnly = true)
    public Mono<User> findById(Long id) {
        return transactionalOperator.execute(status -> super.findById(id)).next();
    }

    @Override
    @Transactional
    public Mono<Void> deleteById(Long id) {
        return transactionalOperator.execute(status -> repository.deleteById(id)).then();
    }
}
