package com.app.whatsApp.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal
            (@NotNull HttpServletRequest request,
             @NotNull HttpServletResponse response,
             @NotNull FilterChain filterChain)
            throws ServletException, IOException {
        Map<String, Object> map = new LinkedHashMap<>();
        ObjectMapper objectMapper = new ObjectMapper();
        try {

            String jwt = getToken(request);
            if (jwt == null) {
                if (request.getRequestURI().startsWith("/api/v1/auth")) {
                    filterChain.doFilter(request, response);
                    return;
                } else {

                    map.put("status", "fail");
                    map.put("message", "You are not authenticated");
                    response.setStatus(HttpStatus.UNAUTHORIZED.value());
                    response.setContentType("application/json");
                    objectMapper.writeValue(response.getWriter(), map);
                    response.getWriter().flush();
                    return;
                }
            }

            String username = jwtService.extractUsername(jwt);
            if (username != null &&
                    SecurityContextHolder.getContext()
                            .getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                if (jwtService.isTokenValid(jwt, userDetails)) {

                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    authenticationToken
                            .setDetails(new WebAuthenticationDetailsSource()
                                    .buildDetails(request));
                    SecurityContextHolder.getContext()
                            .setAuthentication(authenticationToken);

                }

            }

            filterChain.doFilter(request, response);
        } catch (UsernameNotFoundException exc) {
            map.put("status", "fail");
            map.put("message", exc.getMessage());
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType("application/json");
            objectMapper.writeValue(response.getWriter(), map);
            response.getWriter().flush();
        } catch (Exception ex) {
            map.put("status", "error");
            map.put("message", "Internal server error");
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setContentType("application/json");
            objectMapper.writeValue(response.getWriter(), map);
            response.getWriter().flush();
        }

    }

    private String getToken(HttpServletRequest request) {
        String auth = request.getHeader("Authorization");
        if (auth != null && auth.startsWith("Bearer")) {
            return auth.substring(7);
        }
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            return Arrays.stream(cookies)
                    .filter(a -> "jwt".
                            equals(a.getName()))
                    .findFirst().map(Cookie::getValue)
                    .orElse(null);
        }
        return null;
    }
}
