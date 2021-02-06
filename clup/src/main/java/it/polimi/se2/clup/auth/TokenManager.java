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
import java.util.List;

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

    public int verify(String token, List<AuthFlag> authList) throws TokenException {

        int id;
        boolean found = false;
        try {
            DecodedJWT jwt = verifier.verify(token);

            for(AuthFlag flag : authList)
                if(jwt.getClaim("auth").asInt() >= flag.ordinal())
                    found = true;

            if(found)
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

    public AuthFlag getAuthFlag(String token) {
        DecodedJWT jwt = JWT.decode(token);
        return AuthFlag.values()[jwt.getClaim("auth").asInt()];
    }

}
