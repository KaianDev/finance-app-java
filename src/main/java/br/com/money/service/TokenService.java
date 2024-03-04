package br.com.money.service;

import br.com.money.model.User;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TokenService {

    @Value("${api.security.token.secret}")
    private String secret;

    public String getToken(User user) {

        return JWT.create()
                .withIssuer("money")
                .withSubject(user.getUsername())
                .withExpiresAt(LocalDateTime.now().plusHours(1).toInstant(ZoneOffset.of("-03:00")))
                .sign(Algorithm.HMAC256(secret));
    }

    public String getSubject(String token) {
        return JWT.require(Algorithm.HMAC256(secret))
                .withIssuer("money")
                .build()
                .verify(token).getSubject();
    }
}
