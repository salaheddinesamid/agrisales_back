package com.example.medjool.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

@Slf4j
@Component
public class ApiKeyAuthenticationFilter extends OncePerRequestFilter {

    private static final String API_KEY_HEADER = "API_KEY";
    private static final String VALID_API_KEY = "6jQBoznefQ5PeXKj4AcBOWflhb6XV4UcAegQIdti5PLUzz18T2QS1FtgGgX5UQUDtZNpNJUt9NU2XOxiq3gNiZns11Zmvuw5oi8WgNTEW288h9ooK2XVtHCE19TnJMx2"; // Store this securely in config

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {


        String apiKey = request.getHeader(API_KEY_HEADER);  // Extract the API KEY from the client request header

        // If no API key, just continue the filter chain (JWT filter will handle auth)
        if (apiKey == null || apiKey.isEmpty()) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // Check if the API key is valid
            if (isValidApiKey(apiKey)) {
                // If the API key is valid, we create an list of authorities to enable the services access the endpoint
                List<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList("API_SERVICE");
                Authentication auth = new ApiKeyAuthenticationToken(apiKey, authorities);
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
            filterChain.doFilter(request, response);
        } catch (BadCredentialsException e) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid API Key");
        }
    }

    private boolean isValidApiKey(String apiKey) {
        // Implement your API key validation logic here
        // Compare with stored API key (from config/vault/database)
        return VALID_API_KEY.equals(apiKey);
    }

    // Simple Authentication implementation for API key
    private static class ApiKeyAuthenticationToken implements Authentication {
        private final String apiKey;
        private final boolean authenticated;
        private final Object principal;
        private final Object credentials;
        private final Collection<? extends GrantedAuthority> authorities;

        public ApiKeyAuthenticationToken(String apiKey, Collection<? extends GrantedAuthority> authorities) {
            this.apiKey = apiKey;
            this.authenticated = true;
            this.principal = "api-key-user"; // Can be any identifier
            this.credentials = apiKey;
            this.authorities = authorities;
        }

        @Override public Collection<? extends GrantedAuthority> getAuthorities() {
            return authorities;
        }
        @Override public Object getCredentials() { return credentials; }
        @Override public Object getDetails() { return null; }
        @Override public Object getPrincipal() { return principal; }
        @Override public boolean isAuthenticated() { return authenticated; }
        @Override public void setAuthenticated(boolean isAuthenticated) { throw new UnsupportedOperationException(); }
        @Override public String getName() { return (String) principal; }
    }
}