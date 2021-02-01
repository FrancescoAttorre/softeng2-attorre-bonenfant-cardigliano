package it.polimi.se2.clup.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import it.polimi.se2.clup.auth.exceptions.AuthorizationException;
import it.polimi.se2.clup.auth.exceptions.TokenException;

import java.util.Date;

class TokenManager {
    private final String SECRET = "secretCode";
    private final String ISSUER = "clup";

    private final Algorithm algorithm = Algorithm.HMAC512(SECRET);

    private final JWTVerifier verifier;

    public TokenManager() {
        verifier = JWT.require(algorithm)
                .withIssuer(ISSUER)
                .build();

    }

    public String createToken(int id, long expirationTime, AuthFlag auth) {
        String token = JWT.create()
                .withIssuer(ISSUER)
                .withExpiresAt(new Date(System.currentTimeMillis() + expirationTime))
                .withClaim("id", id)
                .withClaim("auth", auth.ordinal())
                .sign(algorithm);

        return token;
    }

    public int verify(String token, AuthFlag auth) throws TokenException {

        int id;
        try {
            DecodedJWT jwt = verifier.verify(token);
            if(jwt.getClaim("auth").asInt() == auth.ordinal())
                id = jwt.getClaim("id").asInt();
            else
                throw new AuthorizationException();
        } catch (TokenExpiredException e) {
            throw new TokenException("Token Expired");
        } catch (JWTVerificationException e) {
            throw new TokenException();
        }
        return id;
    }



}
