package co.com.pragma.model.user;

import co.com.pragma.model.role.Role;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {
    @Test
    void shouldCreateAUserWithArgs() {
        Long id = 1L;
        String name = "Juan";
        String lastname = "Ceballos";
        String email = "juan@example.com";
        LocalDate birthdate = LocalDate.now();
        String address = "Av 45 # 33-85";
        String identityNumber = "12345678";
        String phoneNumber = "3001234567";
        String password = "123456";
        Long baseSalary = 35000000L;

        Role role = new Role(1L, "ADMIN", "Administrador del sistema");

        User user = new User(
                id,
                name,
                lastname,
                birthdate,
                address,
                email,
                identityNumber,
                password,
                phoneNumber,
                baseSalary,
                role.getId()

        );

        assertNotNull(user);
        assertEquals(id, user.getId());
        assertEquals(name, user.getName());
        assertEquals(lastname, user.getLastname());
        assertEquals(email, user.getEmail());
        assertEquals(identityNumber, user.getIdentityNumber());
        assertEquals(phoneNumber, user.getPhoneNumber());
        assertEquals(baseSalary, user.getBaseSalary());
        assertEquals(1L, user.getRoleId());
    }

    @Test
    void shouldCreateARoleWithBuilder() {
        Long id = 1L;
        String name = "Juan";
        String lastname = "Ceballos";
        String email = "juan@example.com";
        LocalDate birthdate = LocalDate.now();
        String address = "Av 45 # 33-85";
        String identityNumber = "12345678";
        String phoneNumber = "3001234567";
        String password = "123456";
        Long baseSalary = 35000000L;

        Role role = Role.builder()
                .id(1L)
                .name("Client")
                .description("Cliente de la plataforma")
                .build();

        User user = User.builder()
                .id(id)
                .name(name)
                .lastname(lastname)
                .email(email)
                .identityNumber(identityNumber)
                .phoneNumber(phoneNumber)
                .roleId(role.getId())
                .baseSalary(baseSalary)
                .build();

        assertNotNull(user);
        assertEquals(id, user.getId());
        assertEquals(name, user.getName());
        assertEquals(lastname, user.getLastname());
        assertEquals(email, user.getEmail());
        assertEquals(identityNumber, user.getIdentityNumber());
        assertEquals(phoneNumber, user.getPhoneNumber());
        assertEquals(baseSalary, user.getBaseSalary());
        assertEquals(1L, user.getRoleId());
    }

    @Test
    void shouldCreateARoleWithSetters() {
        String name = "ADMIN";
        Long id = 1L;
        String description = "Administrador del sistema";

        Role role = new Role(id, name, description);
        role.setName(name);
        role.setDescription(description);

        assertNotNull(role);
        assertEquals(id, role.getId());
        assertEquals(name, role.getName());
        assertEquals(description, role.getDescription());
    }
}
