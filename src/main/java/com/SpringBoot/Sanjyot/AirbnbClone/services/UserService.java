package com.SpringBoot.Sanjyot.AirbnbClone.services;

import com.SpringBoot.Sanjyot.AirbnbClone.entities.UserEntity;
import com.SpringBoot.Sanjyot.AirbnbClone.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new BadCredentialsException("User with email "+ username +" not found"));
    }

    public UserEntity loadUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new BadCredentialsException("User with id "+ id +" not found"));
    }

    public UserEntity loadUserByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    public UserEntity save(UserEntity newUser) {
        return userRepository.save(newUser);
    }
}
