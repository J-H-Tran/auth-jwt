package co.jht.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

@Service
public class JwtService {
    private static final Long EXPIRATION_TIME = 86400000L; // 24 hours
    private static final String BLACKLIST_KEY_PREFIX = "token:blacklist:";

    @Value("${jwt.secret}")
    private String secret;

    private final RedisTemplate<String, String> redisTemplate;

    public JwtService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userDetails.getUsername());
    }

    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getSignInKey())
                .compact();
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        // First check if token is blacklisted
        if (isTokenBlacklisted(token)) {
            return false;
        }
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    // Add token to blacklist with expiration matching the token's expiration
    public void blacklistToken(String token) {
        Claims claims = extractAllClaims(token);
        Date expiration = claims.getExpiration();
        long ttl = (expiration.getTime() - System.currentTimeMillis()) / 1000;

        if (ttl > 0L) {
            String blacklistKey = BLACKLIST_KEY_PREFIX + token;
            redisTemplate.opsForValue().set(blacklistKey, "blacklisted", ttl, TimeUnit.SECONDS);
        }
    }

    // Check if a token is blacklisted
    public boolean isTokenBlacklisted(String token) {
        String blacklistKey = BLACKLIST_KEY_PREFIX + token;
        return Boolean.TRUE.equals(redisTemplate.hasKey(blacklistKey));
    }
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpirationDate(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64URL.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }
}