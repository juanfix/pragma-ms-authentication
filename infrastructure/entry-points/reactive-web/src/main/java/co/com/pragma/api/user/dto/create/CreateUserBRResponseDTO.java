package co.com.pragma.api.user.dto.create;

public record CreateUserBRResponseDTO(
        String timestamp,
        String status,
        String error,
        String message
) {
}
