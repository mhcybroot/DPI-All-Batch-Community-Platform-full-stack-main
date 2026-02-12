package mh.cyb.root.DpiBatchMeetBackend.security;

import org.springframework.stereotype.Component;
import java.security.Key;
import java.util.Date;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtTokenProvider {

    private String jwtSecret = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970"; // Use a strong key
    private long jwtExpirationDate = 86400000; // 24 hours

    public String generateToken(String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationDate);

        return Jwts.builder()
                .subject(username)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(key())
                .compact();
    }

    public String getUsername(String token) {
        return Jwts.parser()
                .verifyWith((javax.crypto.SecretKey) key())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith((javax.crypto.SecretKey) key())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            // Log exception
            return false;
        }
    }

    private Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }
}
