package co.com.pragma.jjwtsecurity.jwt.manager;

import co.com.pragma.jjwtsecurity.jwt.provider.JwtProvider;
import co.com.pragma.model.role.gateways.RoleRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class JwtAuthenticationManager implements ReactiveAuthenticationManager {

    private final JwtProvider jwtProvider;
    private final RoleRepository roleRepository;

    public JwtAuthenticationManager(JwtProvider jwtProvider, RoleRepository roleRepository) {
        this.jwtProvider = jwtProvider;
        this.roleRepository = roleRepository;
    }

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        return Mono.just(authentication)
                .map(auth -> jwtProvider.getClaims(auth.getCredentials().toString()))
                .log()
                .onErrorResume(e -> Mono.error(new BadCredentialsException("Bad or malformed token", e)))
                .map(claims -> {
                    String username = claims.getSubject();
                    Object rolesClaim = claims.get("roles");

                    List<SimpleGrantedAuthority> authorities = extractAuthorities(rolesClaim);

                    return new UsernamePasswordAuthenticationToken(
                            username,
                            null,
                            authorities
                    );
                });
    }

    private List<SimpleGrantedAuthority> extractAuthorities(Object rolesClaim) {
        if (rolesClaim == null) {
            return List.of(new SimpleGrantedAuthority("ROLE_CLIENT"));
        }

        // Si es una lista de Strings
        if (rolesClaim instanceof List) {
            try {
                return ((List<?>) rolesClaim).stream()
                        .filter(Objects::nonNull)
                        .map(Object::toString) // Convertir cada elemento a String
                        .map(role -> {
                            // Asegurar que el rol tenga el prefijo ROLE_ si es necesario
                            if (!role.startsWith("ROLE_")) {
                                return "ROLE_" + role;
                            }
                            return role;
                        })
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());
            } catch (Exception e) {
                System.err.println("Error processing roles list: " + e.getMessage());
                return List.of(new SimpleGrantedAuthority("ROLE_USER"));
            }
        }

        // Si es un String individual
        if (rolesClaim instanceof String) {
            String role = (String) rolesClaim;
            if (!role.startsWith("ROLE_")) {
                role = "ROLE_" + role;
            }
            return List.of(new SimpleGrantedAuthority(role));
        }

        // Para cualquier otro tipo, convertirlo a String
        String role = rolesClaim.toString();
        if (!role.startsWith("ROLE_")) {
            role = "ROLE_" + role;
        }
        return List.of(new SimpleGrantedAuthority(role));
    }
}
