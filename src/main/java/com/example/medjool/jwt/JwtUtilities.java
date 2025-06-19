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

/** * Utility class for handling JWT operations such as token generation, validation, and extraction of claims.
 */


@Slf4j
@Component
public class JwtUtilities {

    // Encryption key:
    private final String secret = "385e7e7bf9074b975ccfb147a035696893be210c823250bf824cf61538176eda";

    // Expiration time:
    private Long jwtExpiration = 36000000L;

    /** Extracts the username from the JWT token.
     *
     * @param token the JWT token
     * @return the username extracted from the token
     */
    public String extractUserName(String token){
        return extractClaim(token, Claims::getSubject);
    }

    /** * Extracts a specific claim from the JWT token.
     *
     * @param token the JWT token
     * @param claimsTFunction a function to extract the desired claim
     * @param <T> the type of the claim to be extracted
     * @return the extracted claim
     */
    private <T> T extractClaim(String token, Function<Claims,T> claimsTFunction) {
        final Claims claims = extractAllClaims(token);
        return claimsTFunction.apply(claims);
    }

    /** * Extracts all claims from the JWT token.
     *
     * @param token the JWT token
     * @return the claims contained in the token
     */
    private Claims extractAllClaims(String token){
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();
    }

    /** * Extracts the expiration date from the JWT token.
     *
     * @param token the JWT token
     * @return the expiration date of the token
     */
    public Date extractExpiration(String token) { return
            extractClaim(token, Claims::getExpiration);
    }

    /** * Checks if the JWT token is expired.
     *
     * @param token the JWT token
     * @return true if the token is expired, false otherwise
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String email = extractUserName(token);
        return (email.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    /** * Checks if the JWT token is expired.
     *
     * @param token the JWT token
     * @return true if the token is expired, false otherwise
     */
    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /** * Retrieves the JWT token from the HTTP request.
     *
     * @param httpServletRequest the HTTP request containing the JWT token
     * @return the JWT token if present, null otherwise
     */
    public String getToken (HttpServletRequest httpServletRequest) {
        final String bearerToken = httpServletRequest. getHeader("Authorization");
        if(StringUtils.hasText(bearerToken) && bearerToken. startsWith("Bearer "))
        {
            return bearerToken.substring(7); } // The part after "Bearer "
        return null;
    }

    /** * Validates the JWT token.
     *
     * @param token the JWT token to validate
     * @return true if the token is valid, false otherwise
     */
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

    /** * Generates a JWT token for the given email and role.
     *
     * @param email the email to include in the token
     * @param role the role to include in the token
     * @return the generated JWT token
     */
    public String generateToken(String email, String role){
        return Jwts.builder()
                .setSubject(email)
                .claim("role",role)
                .setExpiration(new Date(System.currentTimeMillis() + 360000))
                .signWith(SignatureAlgorithm.HS256,secret)
                .compact();
    }

}