package com.example.timecoder.gateway.controller;

import com.example.timecoder.gateway.model.User;
import com.example.timecoder.gateway.payload.auth.UserIdentityAvailability;
import com.example.timecoder.gateway.payload.auth.UserSummary;
import com.example.timecoder.gateway.repository.UserRepository;
import com.example.timecoder.gateway.security.CurrentUser;
import com.example.timecoder.gateway.security.UserPrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static java.util.stream.Collectors.toList;

@RestController
public class UserController {

    @Autowired
    private UserRepository userRepository;

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @GetMapping("/user/me")
    @PreAuthorize("isAuthenticated()")
    public UserSummary getCurrentUser(@CurrentUser UserPrincipal currentUser) {
        List<String> roleList = currentUser.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(toList());
        UserSummary userSummary = new UserSummary(currentUser.getId(), currentUser.getUsername(), currentUser.getName());
        userSummary.setPriviledged(roleList.contains("USER_ADMIN"));
        return userSummary;
    }

    @GetMapping("/user/checkUsernameAvailability")
    public UserIdentityAvailability checkUsernameAvailability(@RequestParam(value = "username") String username) {
        Boolean isAvailable = !userRepository.existsByUsername(username);
        return new UserIdentityAvailability(isAvailable);
    }

    @GetMapping("/user/checkEmailAvailability")
    public UserIdentityAvailability checkEmailAvailability(@RequestParam(value = "email") String email) {
        Boolean isAvailable = !userRepository.existsByEmail(email);
        return new UserIdentityAvailability(isAvailable);
    }

    @GetMapping("/users/list")
    @PreAuthorize("isAuthenticated()")
    public List<User> getAllUsers(){
        return userRepository.findAll();
    }
}
