package com.SpringBoot.Sanjyot.AirbnbClone.security;

import com.SpringBoot.Sanjyot.AirbnbClone.entities.UserEntity;
import com.SpringBoot.Sanjyot.AirbnbClone.entities.enums.Roles;
import com.SpringBoot.Sanjyot.AirbnbClone.repositories.UserRepository;
import com.SpringBoot.Sanjyot.AirbnbClone.services.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final JWTService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserService userService;

    public UserDTO userSignUp(SignupDTO signupDTO) {
        Optional<UserEntity> signupUser = userRepository.findByEmail(signupDTO.getEmail());
        if (signupUser.isPresent()){
            throw new IllegalArgumentException("User with email:"+signupDTO.getEmail()+" is already present");
        }

        UserEntity tobeSavedUser = modelMapper.map(signupDTO, UserEntity.class);

        if (signupDTO.getRole() == null ||
                !signupDTO.getRole().contains(Roles.HOTEL_MANAGER)) {

            tobeSavedUser.setRole(Set.of(Roles.GUEST));
        } else {
            tobeSavedUser.setRole(Set.of(Roles.HOTEL_MANAGER));
        }

        tobeSavedUser.setPassword(passwordEncoder.encode(tobeSavedUser.getPassword()));
        System.out.println(signupDTO);
        UserEntity savedUser = userRepository.save(tobeSavedUser);
        return modelMapper.map(savedUser, UserDTO.class);
    }

    public LoginResponseDTO userLogin(LoginDTO loginDTO) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDTO.getEmail(),loginDTO.getPassword()));

        UserEntity user = (UserEntity) authentication.getPrincipal();

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return new LoginResponseDTO(user.getId(),accessToken,refreshToken);
    }

    public LoginResponseDTO refreshToken(String refreshToken) {
        Long userId = jwtService.getUserIdFromToken(refreshToken);
        UserEntity user = userService.loadUserById(userId);

        String accessToken = jwtService.generateAccessToken(user);

        return new LoginResponseDTO(user.getId(),accessToken,refreshToken);
    }
}
