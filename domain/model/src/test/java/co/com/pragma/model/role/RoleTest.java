package co.com.pragma.model.role;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RoleTest {

    @Test
    void shouldCreateARoleWithArgs() {
        String name = "ADMIN";
        Long id = 1L;
        String description = "Administrador del sistema";
        Role role = new Role(id, name, description);

        assertNotNull(role);
        assertEquals(id, role.getId());
        assertEquals(name, role.getName());
        assertEquals(description, role.getDescription());
    }

    @Test
    void shouldCreateARoleWithBuilder() {
        String name = "ADMIN";
        Long id = 1L;
        String description = "Administrador del sistema";

        Role role = Role.builder()
                .id(id)
                .name(name)
                .description(description)
                .build();

        assertNotNull(role);
        assertEquals(id, role.getId());
        assertEquals(name, role.getName());
        assertEquals(description, role.getDescription());
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
