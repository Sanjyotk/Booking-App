package com.SpringBoot.Sanjyot.AirbnbClone.security;


import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class JWTController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<UserDTO> userSignUp(@RequestBody SignupDTO signupDTO){
        UserDTO user = authService.userSignUp(signupDTO);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> userLogin(@RequestBody LoginDTO loginDTO,
                                                      HttpServletRequest request,
                                                      HttpServletResponse response){

        LoginResponseDTO loginResponseDTO = authService.userLogin(loginDTO);
        Cookie cookie = new Cookie("RefreshToken",loginResponseDTO.getRefreshToken());
        //so as to only accessed by http and not by any JS
        cookie.setHttpOnly(true);

        response.addCookie(cookie);

        return ResponseEntity.ok(loginResponseDTO);
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponseDTO> refreshAccesstoken(HttpServletRequest request,
                                                               HttpServletResponse response){

        String refreshToken = extractRefreshTokenFromHeader(request);
        LoginResponseDTO loginResponseDTO = authService.refreshToken(refreshToken);

        return ResponseEntity.ok(loginResponseDTO);
    }

    @PostMapping("/logout")
    public ResponseEntity<Boolean> logoutSession(HttpServletRequest request,
                                                 HttpServletResponse response){

        String refreshToken = extractRefreshTokenFromHeader(request);
        Cookie cookie = new Cookie("RefreshToken",null);
        cookie.setHttpOnly(true);
        response.addCookie(cookie);

        return ResponseEntity.ok(true);
    }

    public String extractRefreshTokenFromHeader(HttpServletRequest request){
        return Arrays.stream(request.getCookies())
                .filter(cookie->"RefreshToken".equals(cookie.getName()))
                .findFirst()
                .map(Cookie::getValue)
                .orElseThrow(() -> new AuthenticationServiceException("refresh token is invalid"));
    }
}
