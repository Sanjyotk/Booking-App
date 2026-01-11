package com.SpringBoot.Sanjyot.AirbnbClone.security;

import com.SpringBoot.Sanjyot.AirbnbClone.entities.UserEntity;
import com.SpringBoot.Sanjyot.AirbnbClone.services.UserService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JWTAuthFilter extends OncePerRequestFilter {

    private final JWTService jwtService;
    private final UserService userService;

    @Autowired
    @Qualifier("handlerExceptionResolver")
    private final HandlerExceptionResolver handlerExceptionResolver;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String requestHeader = request.getHeader("Authorization");
            if (requestHeader == null) {
                filterChain.doFilter(request, response);
                return;
            }
            String jwtToken = requestHeader.split("Bearer ")[1];

            Long userId = jwtService.getUserIdFromToken(jwtToken);
            String tokenType = jwtService.getTokenTypeFromToken(jwtToken);

            if (!"Access".equals(tokenType)) throw new JwtException("Invalid Token Type");

            if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserEntity user = userService.loadUserById(userId);
                UsernamePasswordAuthenticationToken authenticationToken = UsernamePasswordAuthenticationToken.authenticated(user, null, user.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }

            filterChain.doFilter(request, response);
        } catch (JwtException e) {
            handlerExceptionResolver.resolveException(request,response,null,e);
        }
    }
}
