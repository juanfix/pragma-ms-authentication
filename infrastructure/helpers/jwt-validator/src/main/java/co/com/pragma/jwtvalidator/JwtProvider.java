package co.com.pragma.jwtvalidator;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;

@Component
public class JwtProvider {
    private final Key key;
    private final Long expirationTime;
    private static final Logger log = LoggerFactory.getLogger(JwtProvider.class);

    public JwtProvider(MicroserviceProps props) {
        this.key = Keys.hmacShaKeyFor(props.getJwtSecretKey().getBytes(StandardCharsets.UTF_8));
        this.expirationTime = props.getJwtExpiration();
    }

    public Mono<Boolean> validateToken(String token) {
        return Mono.fromCallable(() -> {
            try {
                Jwts.parser()
                        .verifyWith((SecretKey) key)
                        .build()
                        .parseClaimsJws(token)
                        .getPayload();
                return true;
            } catch (JwtException | IllegalArgumentException e) {
                log.error("❌ Token inválido: {}", e.getMessage());
                return false;
            }
        });
    }
}
