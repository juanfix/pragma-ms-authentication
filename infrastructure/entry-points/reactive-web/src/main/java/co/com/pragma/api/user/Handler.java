package co.com.pragma.api.user;

import co.com.pragma.api.user.dto.create.CreateUserRequestDTO;
import co.com.pragma.model.user.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import co.com.pragma.usecase.user.UserUseCase;
import org.springframework.http.MediaType;
import org.springframework.scheduling.config.Task;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
@RequiredArgsConstructor
@RestController
@Tag(name = "User", description = "User endpoints")
public class Handler {
    private final UserUseCase userUseCase;

    @PostMapping(path = "/api/v1/user", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Create a new user",
            description = "Returns the information about the new client created.",
            requestBody = @RequestBody(
                    required = true,
                    description = "User information required.",
                    content = @Content(schema = @Schema(implementation = CreateUserRequestDTO.class))
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Created",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Map.class)
                            )
                    )
            }
    )
    public Mono<ServerResponse> listenSaveUser(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(User.class)
                .flatMap(userUseCase::saveUser)
                .flatMap(savedTask -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(savedTask));
    }

    public Mono<ServerResponse> listenUpdateUser(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(User.class)
                .flatMap(userUseCase::updateUser)
                .flatMap(savedUser -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(savedUser));
    }

    public Mono<ServerResponse> listenGetAllUser(ServerRequest serverRequest) {
        return ServerResponse.ok()
                //.contentType(MediaType.APPLICATION_JSON)
                //.contentType(MediaType.APPLICATION_NDJSON)
                .contentType(MediaType.TEXT_EVENT_STREAM)
                .body(userUseCase.getAllUsers(), Task.class);
    }

    public Mono<ServerResponse> listenGetUserByIdentityNumber(ServerRequest serverRequest) {
        String id = serverRequest.pathVariable("id");

        return userUseCase.getUserByIdentityNumber(id)
                .flatMap(task -> ServerResponse.ok()
                        .contentType(MediaType.TEXT_EVENT_STREAM)
                        .bodyValue(task))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> listenDeleteUser(ServerRequest serverRequest) {
        Long id = Long.parseLong(serverRequest.pathVariable("id"));

        return userUseCase.deleteUser(id)
                .then(ServerResponse.noContent().build());
    }
}
