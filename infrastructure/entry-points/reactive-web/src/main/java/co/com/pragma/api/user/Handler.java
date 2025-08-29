package co.com.pragma.api.user;

import co.com.pragma.api.user.dto.create.CreateUserFailResponseDTO;
import co.com.pragma.api.user.dto.create.CreateUserRequestDTO;
import co.com.pragma.api.user.dto.create.CreateUserResponseDTO;
import co.com.pragma.model.user.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import co.com.pragma.usecase.user.UserUseCase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.scheduling.config.Task;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.util.UriComponentsBuilder;

import reactor.core.publisher.Mono;

import java.net.URI;

@Component
@RequiredArgsConstructor
@RestController
@Tag(name = "User", description = "User endpoints")
@Slf4j
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
                            responseCode = "200",
                            description = "Created",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = CreateUserResponseDTO.class)
                            )
                    ),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = CreateUserFailResponseDTO.class)))
            }
    )
    public Mono<ServerResponse> listenSaveUser(ServerRequest serverRequest) {
        String id = serverRequest.pathVariable("id");
        URI url = UriComponentsBuilder.fromUriString("/api/v1/user{id}").buildAndExpand(id).toUri();
        return serverRequest.bodyToMono(User.class)
                .doOnNext(user -> log.info("📥 Se va a resigtrar el usuario: {}", user))
                .flatMap(userUseCase::saveUser)
                .doOnNext(savedUser -> log.info("✅ usuario almacenado en la base de datos: {}", savedUser))
                .flatMap(savedUser -> ServerResponse.created(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(savedUser));
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
