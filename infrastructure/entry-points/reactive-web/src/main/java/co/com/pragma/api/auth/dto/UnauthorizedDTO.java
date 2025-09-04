package co.com.pragma.api.auth.dto;

public record UnauthorizedDTO(
        String timestamp,
        String status,
        String error,
        String message
) {
}
