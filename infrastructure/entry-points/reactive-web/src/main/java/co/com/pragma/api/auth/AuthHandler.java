package co.com.pragma.api.auth;

import co.com.pragma.api.auth.dto.UnauthorizedDTO;
import co.com.pragma.api.user.dto.create.CreateUserFailResponseDTO;
import co.com.pragma.model.user.dto.LoginDTO;
import co.com.pragma.model.user.dto.TokenDTO;
import co.com.pragma.usecase.user.user.LoginUseCaseInterface;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
@RestController
@Tag(name = "Auth", description = "Auth endpoints")
@Slf4j
public class AuthHandler {
    private final LoginUseCaseInterface loginUseCase;

    @PostMapping(path = "/api/v1/login", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Login an user",
            description = "Returns a token if the user is validated correctly.",
            requestBody = @RequestBody(
                    required = true,
                    description = "User information required.",
                    content = @Content(schema = @Schema(implementation = LoginDTO.class))
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Ok",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = TokenDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized",
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
    public Mono<ServerResponse> listenLoginUser(ServerRequest serverRequest) {
        log.info("Iniciando login de usuario");

        return serverRequest.bodyToMono(LoginDTO.class)
                .doOnNext(dto -> log.debug("Se reciben datos del login: {}", dto))
                .flatMap(dto -> loginUseCase.login(new LoginDTO(dto.email(), dto.password()))
                        .flatMap(resp -> ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(resp))
                )
                .onErrorResume(IllegalArgumentException.class, e -> {
                    log.warn("Error de credenciales: {}", e.getMessage());
                    Map<String, Object> errorResponse = new HashMap<>();
                    errorResponse.put("timestamp", LocalDateTime.now().toString());
                    errorResponse.put("status", 401);
                    errorResponse.put("error", "Unauthorized");
                    errorResponse.put("message", e.getMessage());
                    return ServerResponse.status(HttpStatus.UNAUTHORIZED)
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(errorResponse);
                })
                .onErrorResume(e -> {
                    log.error("Error interno en login", e);
                    return ServerResponse.status(500)
                            .bodyValue(Map.of("error", "Error interno: " + e.getMessage()));
                });
    }
}
