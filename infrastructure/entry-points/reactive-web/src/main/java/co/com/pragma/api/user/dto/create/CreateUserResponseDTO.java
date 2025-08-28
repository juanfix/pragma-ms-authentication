package co.com.pragma.api.user.dto.create;

public record CreateUserResponseDTO(
        Long id,
        String name,
        String lastname,
        String birthDate,
        String address,
        String email,
        String identityNumber,
        String phoneNumber,
        Long baseSalary,
        Long roleId
) {
}
