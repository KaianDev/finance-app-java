package br.com.money.service;

import br.com.money.model.User;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
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
                .withIssuer("fnce.")
                .withSubject(user.getUsername())
                .withClaim("name", user.getName())
                .withExpiresAt(LocalDateTime.now().plusHours(1).toInstant(ZoneOffset.of("-03:00")))
                .sign(Algorithm.HMAC256(secret));
    }

    public String unauthorizedUserToken(User user) {
        return JWT.create()
                .withClaim("name", user.getName())
                .withClaim("email", user.getEmail())
                .withClaim("password", user.getPassword())
                .withClaim("code", user.getCode())
                .sign(Algorithm.HMAC256(secret));
    }

    public String getSubject(String token) {
        try {
            return JWT.require(Algorithm.HMAC256(secret))
                    .withIssuer("fnce.")
                    .build()
                    .verify(token).getSubject();
        }catch (JWTVerificationException e) {
            return "";
        }
    }

    public DecodedJWT decoded(String token) {
        return JWT.decode(token);
    }
}
