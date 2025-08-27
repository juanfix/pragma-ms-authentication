package co.com.pragma.model.user;

import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class User {
    private Long id;
    private String name;
    private String lastname;
    private String birthDate;
    private String address;
    private String email;
    private String identityNumber;
    private String phoneNumber;
    private Long baseSalary;
    private Long roleId;
}
