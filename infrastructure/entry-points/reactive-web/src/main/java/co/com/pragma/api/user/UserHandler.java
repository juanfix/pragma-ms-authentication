package co.com.pragma.api.user;

import co.com.pragma.api.auth.dto.UnauthorizedDTO;
import co.com.pragma.api.user.dto.create.CreateUserBRResponseDTO;
import co.com.pragma.api.user.dto.create.CreateUserFailResponseDTO;
import co.com.pragma.api.user.dto.create.CreateUserRequestDTO;
import co.com.pragma.api.user.dto.create.CreateUserResponseDTO;
import co.com.pragma.api.user.dto.validate.ValidateUserRequest;
import co.com.pragma.model.user.User;
import co.com.pragma.usecase.user.jwt.JwtUseCaseInterface;
import co.com.pragma.usecase.user.user.FindUserUseCase;
import co.com.pragma.usecase.user.user.FindUserUseCaseInterface;
import co.com.pragma.usecase.user.user.SaveUserUseCaseInterface;
import co.com.pragma.usecase.user.user.UpdateUserUseCaseInterface;
import co.com.pragma.usecase.user.user.validations.error.UserValidationException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.scheduling.config.Task;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.util.UriComponentsBuilder;

import reactor.core.publisher.Mono;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
@RestController
@Tag(name = "User", description = "User endpoints")
@Slf4j
public class UserHandler {
    private final SaveUserUseCaseInterface saveUserUseCaseInterface;
    private final UpdateUserUseCaseInterface updateUserUseCaseInterface;
    private final FindUserUseCaseInterface findUserUseCaseInterface;
    private final JwtUseCaseInterface jwtUseCase;

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
                                    schema = @Schema(implementation = CreateUserResponseDTO.class)
                            )
                    ),
                    @ApiResponse(responseCode = "400", description = "Bad request",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = CreateUserBRResponseDTO.class))),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = UnauthorizedDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = UnauthorizedDTO.class)
                            )
                    ),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = CreateUserFailResponseDTO.class)))
            }
    )
    @SecurityRequirement(name = "Authorization")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'ASESOR')")
    public Mono<ServerResponse> listenSaveUser(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(User.class)
                .doOnNext(user -> log.info("📥 Se va a resistrar el usuario: {}", user))
                .flatMap(saveUserUseCaseInterface::execute)
                .doOnNext(savedUser -> log.info("✅ usuario almacenado en la base de datos: {}", savedUser))
                .flatMap(savedUser -> {
                    URI url = UriComponentsBuilder.fromUriString("/api/v1/user{id}").buildAndExpand(savedUser.getId()).toUri();
                    return ServerResponse.created(url)
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(savedUser);
                })
                .onErrorResume(UserValidationException.class, e -> {
                    log.warn("Error al validar los datos del usuario: {}", e.getMessage());
                    Map<String, Object> errorResponse = new HashMap<>();
                    errorResponse.put("status", 400);
                    errorResponse.put("timestamp", LocalDateTime.now().toString());
                    errorResponse.put("message", "Error de validación");
                    errorResponse.put("error", e.getMessage());
                    return ServerResponse.badRequest().bodyValue(errorResponse);
                });
    }

    @PostMapping(path = "/api/v1/user/validate", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Validate an user",
            description = "Returns a boolean if the user is validated correctly.",
            requestBody = @RequestBody(
                    required = true,
                    description = "User information required.",
                    content = @Content(schema = @Schema(implementation = ValidateUserRequest.class))
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Ok",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ValidateUserRequest.class)
                            )
                    ),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = CreateUserFailResponseDTO.class)))
            }
    )
    @SecurityRequirement(name = "Authorization")
    public Mono<ServerResponse> listenValidateUser(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(ValidateUserRequest.class)
                .doOnNext(user -> log.info("📥 Se va a validar el usuario: {}", user))
                .flatMap(validateUserRequest -> jwtUseCase.isValidUser(
                                serverRequest.headers().firstHeader("Authorization"),
                                validateUserRequest.getIdentityNumber(),
                                validateUserRequest.getEmail()
                        )
                        .doOnNext(isValid -> log.info("Usuario validado [{}]: {}", validateUserRequest.getEmail(), isValid))
                        .flatMap(isValid -> {
                            if (Boolean.TRUE.equals(isValid)) {
                                return ServerResponse.ok()
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .bodyValue(true);
                            } else {
                                return ServerResponse.status(401)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .bodyValue(false);
                            }
                        }))
                .doOnNext(savedUser -> log.info("✅ usuario validado correctamente: {}", savedUser))
                .onErrorResume(e -> {
                    log.error("Error al validar el usuario", e);
                    return ServerResponse.status(500).bodyValue("Internal server error: " + e.getMessage());
                });
    }

    public Mono<ServerResponse> listenUpdateUser(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(User.class)
                .flatMap(updateUserUseCaseInterface::execute)
                .flatMap(savedUser -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(savedUser));
    }

    public Mono<ServerResponse> listenGetAllUser(ServerRequest serverRequest) {
        return ServerResponse.ok()
                //.contentType(MediaType.APPLICATION_JSON)
                //.contentType(MediaType.APPLICATION_NDJSON)
                .contentType(MediaType.TEXT_EVENT_STREAM)
                .body(findUserUseCaseInterface.getAllUsers(), Task.class);
    }

    public Mono<ServerResponse> listenGetUserByIdentityNumber(ServerRequest serverRequest) {
        String id = serverRequest.pathVariable("id");

        return findUserUseCaseInterface.getUserByIdentityNumber(id)
                .flatMap(task -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(task))
                .switchIfEmpty(ServerResponse.notFound().build());
    }
}
