package co.com.pragma.r2dbc;

import co.com.pragma.model.role.Role;
import co.com.pragma.model.role.gateways.RoleRepository;
import co.com.pragma.r2dbc.entity.RoleEntity;
import co.com.pragma.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class RoleRepositoryAdapter extends ReactiveAdapterOperations<
        Role/* change for domain model */,
        RoleEntity/* change for adapter model */,
        Long,
        RoleReactiveRepository
        > implements RoleRepository {

    private final TransactionalOperator transactionalOperator;

    public RoleRepositoryAdapter(RoleReactiveRepository repository, ObjectMapper mapper, TransactionalOperator transactionalOperator) {
        /**
         *  Could be use mapper.mapBuilder if your domain model implement builder pattern
         *  super(repository, mapper, d -> mapper.mapBuilder(d,ObjectModel.ObjectModelBuilder.class).build());
         *  Or using mapper.map with the class of the object model
         */
        super(repository, mapper, d -> mapper.map(d, Role.class));
        this.transactionalOperator = transactionalOperator;
    }

    @Override
    public Mono<Role> saveRole(Role role) {
        return null;
    }

    @Override
    public Flux<Role> getAllRoles() {
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<Role> getRoleById(Long id) {
        return transactionalOperator.execute(status -> super.findById(id)).next();
    }

    @Override
    public Mono<Role> editRole(Role user) {
        return null;
    }

    @Override
    public Mono<Void> deleteRole() {
        return null;
    }
}
