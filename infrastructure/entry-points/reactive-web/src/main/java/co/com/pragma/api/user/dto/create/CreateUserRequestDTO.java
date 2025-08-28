package co.com.pragma.api.user.dto.create;

import io.swagger.v3.oas.annotations.media.Schema;

public record CreateUserRequestDTO(
    @Schema(
            description = "Users' name",
            example = "Juan",
            minLength = 2,
            maxLength = 150
    )
    String name,

    @Schema(
            description = "Lastname's user",
            example = "Ceballos",
            minLength = 2,
            maxLength = 150
    )
    String lastname,

    @Schema(
            description = "Birthdate's user",
            example = "1991-02-25"
    )
    String birthDate,

    @Schema(
            description = "Address of the user",
            example = "Av 45 # 33-85"
    )
    String address,

    @Schema(
            description = "Email's user",
            example = "juan@mail.com",
            format = "email"
    )
    String email,

    @Schema(
            description = "DNI of the user",
            example = "123456",
            minLength = 6,
            maxLength = 20
    )
    String identityNumber,

    @Schema(
            description = "Phone number of the user",
            example = "3152145214"
    )
    String phoneNumber,

    @Schema(
            description = "Base salary of the user",
            example = "1000000",
            minimum = "0",
            maximum = "15000000"
    )
    Long baseSalary,

    @Schema(
            description = "Role id assigned to the user.",
            example = "1",
            minimum = "0"
    )
    Long roleId
) {}
