package com.alba.master.security;

import com.alba.master.exception.TokenExpiredException;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider tokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String jwt = extractToken(request);

            if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
                // read everything from JWT claims — zero DB calls
                Claims claims = tokenProvider.getClaims(jwt);

                String email      = claims.getSubject();
                Long   employeeId = claims.get("employeeId",   Long.class);
                String empCode    = claims.get("employeeCode", String.class);
                String fullName   = claims.get("fullName",     String.class);

                @SuppressWarnings("unchecked")
                List<String> roles = claims.get("roles", List.class);

                List<SimpleGrantedAuthority> authorities = roles == null ? List.of()
                        : roles.stream()
                               .map(r -> new SimpleGrantedAuthority("ROLE_" + r))
                               .toList();

                EmployeeContext ctx = EmployeeContext.builder()
                        .employeeId(employeeId)
                        .email(email)
                        .employeeCode(empCode)
                        .employeeName(fullName)
                        .roleName(roles != null && !roles.isEmpty() ? roles.get(0) : null)
                        .build();
                EmployeeContextHolder.set(ctx);

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(email, null, authorities);
                authentication.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

            filterChain.doFilter(request, response);

        } catch (TokenExpiredException ex) {
            writeError(response, "TOKEN_EXPIRED", "Your session has expired. Please login again.");
        } catch (Exception ex) {
            writeError(response, "UNAUTHORIZED", "Invalid authentication token");
        } finally {
            EmployeeContextHolder.clear();
        }
    }

    private String extractToken(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        if (StringUtils.hasText(bearer) && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        return null;
    }

    private void writeError(HttpServletResponse response,
                            String error, String message) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(
                "{\"status\":401,\"error\":\"%s\",\"message\":\"%s\"}"
                        .formatted(error, message));
    }
}
