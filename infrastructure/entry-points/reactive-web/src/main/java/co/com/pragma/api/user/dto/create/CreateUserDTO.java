package co.com.pragma.api.user.dto.create;

import java.time.LocalDate;

public record CreateUserDTO(Long id, String name, String lastName, LocalDate birthDate,
                            String email, String identityNumber, String phoneNumber, String password, Long baseSalary, Long roleId) {
}