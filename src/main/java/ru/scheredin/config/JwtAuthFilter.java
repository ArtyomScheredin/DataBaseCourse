package ru.scheredin.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

@Component
@AllArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
    private final UserDetailsService userDetailsService;
    private final JwtUtils jwtUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String userName = null;
        final String jwtToken;
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            filterChain.doFilter(request, response);
            return;
        }

        Optional<Cookie> cookie = Arrays.stream(cookies).filter(c -> c.getName().equals("jwt")).findAny();

        if (cookie.isEmpty()) {
            filterChain.doFilter(request, response);
            return;
        }

        jwtToken = cookie.get().getValue();
        try {
            userName = jwtUtils.extractUsername(jwtToken);
        } catch (Exception e) {
            //ignored
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (userName != null && authentication == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(userName);
            if (jwtUtils.isTokenValid(jwtToken, userDetails)) {
                UsernamePasswordAuthenticationToken authToken
                        = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            } else {
                Cookie cookieToRemove = new Cookie("jwt", "");
                cookieToRemove.setMaxAge(0);
                response.addCookie(cookieToRemove);
            }
        }
        filterChain.doFilter(request, response);
    }
}
