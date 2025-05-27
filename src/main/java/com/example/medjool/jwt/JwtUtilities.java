package com.example.medjool.jwt;

import com.example.medjool.exception.TokenExpiredException;
import io.jsonwebtoken.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import java.util.Date;
import java.util.function.Function;

@Slf4j
@Component
public class JwtUtilities {

    // Encryption key:
    private final String secret = "385e7e7bf9074b975ccfb147a035696893be210c823250bf824cf61538176eda";

    // Expiration time:
    private Long jwtExpiration = 36000000L;

    // Extract the username
    public String extractUserName(String token){
        return extractClaim(token, Claims::getSubject);
    }

    // Extract the claims
    private <T> T extractClaim(String token, Function<Claims,T> claimsTFunction) {
        final Claims claims = extractAllClaims(token);
        return claimsTFunction.apply(claims);
    }
    private Claims extractAllClaims(String token){
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();
    }

    // Extract the expiration date of the token
    public Date extractExpiration(String token) { return
            extractClaim(token, Claims::getExpiration);
    }
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String email = extractUserName(token);
        return (email.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    // Checks if the token is expired.
    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // Get the token from HTTP request.
    public String getToken (HttpServletRequest httpServletRequest) {
        final String bearerToken = httpServletRequest. getHeader("Authorization");
        if(StringUtils.hasText(bearerToken) && bearerToken. startsWith("Bearer "))
        {
            return bearerToken.substring(7); } // The part after "Bearer "
        return null;
    }

    // Validate the token
    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(secret).parseClaimsJws(token);
            return true;
        } catch (MalformedJwtException e) {
            log.info("Invalid JWT token.");
            log.trace("Invalid JWT token trace: {}", e);
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT token.");
            throw new TokenExpiredException("Token has expired");
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT token.");
            log.trace("Unsupported JWT token trace: {}", e);
        } catch (IllegalArgumentException e) {
            log.info("JWT token compact of handler are invalid."); log.trace("JWT token compact of handler are invalid trace: {}", e);
        }
        return false;
    }

    // Generate token for authenticated users
    public String generateToken(String email, String role){
        return Jwts.builder()
                .setSubject(email)
                .claim("role",role)
                .setExpiration(new Date(System.currentTimeMillis() + 360000))
                .signWith(SignatureAlgorithm.HS256,secret)
                .compact();
    }

}