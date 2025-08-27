package co.com.pragma.r2dbc.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;

@Table("users")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UserEntity {
    @Id
    private Long id;
    private String name;
    @Column("last_name")
    private String lastname;
    @Column("birth_date")
    private LocalDate birthDate;
    private String address;
    private String email;
    @Column("identity_number")
    private String identityNumber;
    @Column("phone_number")
    private String phoneNumber;
    private Long baseSalary;
    @Column("role_id")
    private Long roleId;

    @Transient
    private RoleEntity role;

}
