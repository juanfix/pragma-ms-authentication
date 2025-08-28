package co.com.pragma.api.user.dto.create;

public record CreateUserFailResponseDTO(
        String timestamp,
        String path,
        String status,
        String error,
        String requestId,
        String message,
        String trace
) {
}
