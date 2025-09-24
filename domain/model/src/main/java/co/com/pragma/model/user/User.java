package co.com.pragma.model.user;

import co.com.pragma.model.role.Role;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class User {
    private Long id;
    private String name;
    private String lastname;
    private LocalDate birthdate;
    private String address;
    private String email;
    private String identityNumber;
    private String password;
    private String phoneNumber;
    private Long baseSalary;
    private Long roleId;
}
