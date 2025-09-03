package co.com.pragma.api.user.dto.validate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ValidateUserRequest {
    private String identityNumber;
    private String email;
}
