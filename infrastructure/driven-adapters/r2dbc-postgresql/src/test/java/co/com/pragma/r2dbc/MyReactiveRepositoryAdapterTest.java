package co.com.pragma.r2dbc;

import co.com.pragma.model.user.User;
import co.com.pragma.r2dbc.entity.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivecommons.utils.ObjectMapper;
import org.reactivestreams.Publisher;
import org.springframework.transaction.reactive.TransactionCallback;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MyReactiveRepositoryAdapterTest {
    // TODO: change four you own tests

    @InjectMocks
    UserRepositoryAdapter repositoryAdapter;

    @Mock
    UserReactiveRepository repository;

    @Mock
    ObjectMapper mapper;

    @Mock
    TransactionalOperator transactionalOperator;

    private UserRepositoryAdapter adapter;

    private final UserEntity userEntity = UserEntity.builder()
            .id(1L)
            .name("Juan")
            .lastname("Ceballos")
            .email("juan@mail.com")
            .identityNumber("123456")
            .phoneNumber("2249770")
            .baseSalary(120000L)
            .roleId(1L)
            .build();

    private final User user = User.builder()
            .id(1L)
            .name("Juan")
            .lastname("Ceballos")
            .email("juan@mail.com")
            .identityNumber("123456")
            .phoneNumber("2249770")
            .baseSalary(120000L)
            .roleId(1L)
            .build();

    private final User otherUser = User.builder()
            .id(2L)
            .name("Diana")
            .lastname("Martinez")
            .email("diana@mail.com")
            .identityNumber("654321")
            .phoneNumber("2250627")
            .baseSalary(-2L)
            .roleId(2L)
            .build();

    private final User nullUser = User.builder()
            .id(3L)
            .name(null)
            .lastname("Martinez")
            .email("diana@mail.com")
            .identityNumber("654321")
            .phoneNumber("2250627")
            .baseSalary(-2L)
            .roleId(2L)
            .build();

    @BeforeEach
    void setUp() {
        adapter = new UserRepositoryAdapter(repository, mapper, transactionalOperator);
        when(transactionalOperator.execute(any()))
                .thenAnswer(invocation -> {
                    @SuppressWarnings("unchecked")
                    TransactionCallback<?> callback = invocation.getArgument(0);
                    Publisher<?> publisher = callback.doInTransaction(null);
                    return Flux.from(publisher);
                });
    }

    @Test
    void mustFindValueById() {
        when(mapper.map(userEntity, User.class)).thenReturn(user);
        when(repository.findById(1L)).thenReturn(Mono.just(userEntity));

        Mono<User> result = adapter.findById(1L);

        StepVerifier.create(result)
                .expectNextMatches(user -> user.getId().equals(1L) && user.getName().equals("Juan"))
                .verifyComplete();
    }

    @Test
    void mustFindAllValues() {
        when(mapper.map(userEntity, User.class)).thenReturn(user);
        when(repository.findAll()).thenReturn(Flux.just(userEntity));

        Flux<User> result = adapter.findAll();

        StepVerifier.create(result)
                .expectNext(user)
                .verifyComplete();
    }

    @Test
    void mustSaveValue() {
        when(mapper.map(userEntity, User.class)).thenReturn(user);
        when(mapper.map(user, UserEntity.class)).thenReturn(userEntity);
        when(repository.save(userEntity)).thenReturn(Mono.just(userEntity));

        Mono<User> result = adapter.save(user);

        StepVerifier.create(result)
                .expectNext(user)
                .verifyComplete();
    }

    @Test
    void mustReturnEmptyWhenUserNotFoundById() {
        when(repository.findById(99L)).thenReturn(Mono.empty());

        Mono<User> result = adapter.findById(99L);

        StepVerifier.create(result)
                .verifyComplete();
    }

}
