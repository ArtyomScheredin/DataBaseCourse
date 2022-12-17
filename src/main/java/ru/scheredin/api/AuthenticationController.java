package ru.scheredin.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.scheredin.config.JwtUtils;
import ru.scheredin.dto.AuthenticationRequest;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthenticationController {
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtUtils jwtUtils;
    private final ObjectMapper objectMapper;

    @PostMapping
    public ResponseEntity<String> authenticate(@RequestBody AuthenticationRequest request, HttpServletResponse response) throws JsonProcessingException {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getName(), request.getPassword())
        );
        final UserDetails user = userDetailsService.loadUserByUsername(request.getName());
        if (user != null) {
            String token = jwtUtils.generateToken(user);
            if (jwtUtils.isTokenValid(token, user)) {
                Cookie cookie = new Cookie("jwt", token);
                cookie.setMaxAge(24 * 60 * 60);
                cookie.setHttpOnly(false);
                cookie.setSecure(false);
                cookie.setPath("/");
                response.addCookie(cookie);
                return ResponseEntity.ok(token);
            }
        }
        return ResponseEntity.status(401).build();
    }
}
