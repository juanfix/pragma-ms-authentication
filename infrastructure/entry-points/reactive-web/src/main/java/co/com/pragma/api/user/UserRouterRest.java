package co.com.pragma.api.user;

import co.com.pragma.api.config.GlobalPath;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
@RequiredArgsConstructor
public class UserRouterRest {

    private final GlobalPath globalPath;
    private final UserHandler userHandler;

    @Bean
    public RouterFunction<ServerResponse> routerFunction(UserHandler handler) {
        return route(POST(globalPath.getGlobal() + "/user"), userHandler::listenSaveUser)
                .andRoute(POST(globalPath.getGlobal() +"/user/validate"), handler::listenValidateUser)
                //.andRoute(POST(globalPath.getGlobal() +"/login"), handler::listenLoginUser)
                .andRoute(PUT(globalPath.getGlobal() + "/user/{id}"), userHandler::listenUpdateUser)
                .andRoute(GET(globalPath.getGlobal()+ "/user"), userHandler::listenGetAllUser)
                .andRoute(GET(globalPath.getGlobal() + "/user/{id}"), userHandler::listenGetUserByIdentityNumber);
    }
}
