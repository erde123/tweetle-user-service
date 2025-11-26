package nl.fontys.tweetleuserservice.controller;

import nl.fontys.tweetleuserservice.business.service.PublishService;
import nl.fontys.tweetleuserservice.business.service.UserService;
import nl.fontys.tweetleuserservice.domain.UserEvent;
import nl.fontys.tweetleuserservice.domain.UserSyncResult;
import nl.fontys.tweetleuserservice.persistence.entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private PublishService publishService;

    String errorText = "User not found";

    @GetMapping
    public ResponseEntity<List<UserEntity>> getAllUsers() {
        return ResponseEntity.ok(userService.findAll());
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<Object> getUserById(@PathVariable("id") Long id) {
        UserEntity user = userService.findById(id);
        if (user == null) {
            return ResponseEntity.status(404).body(errorText);
        } else {
            return ResponseEntity.ok(user);
        }
    }

    @GetMapping("/auth0/{auth0Id}")
    public ResponseEntity<Object> getUserByAuth0Id(@PathVariable("auth0Id") String auth0Id) {
        UserEntity user = userService.findByAuth0Id(auth0Id);
        if (user == null) {
            return ResponseEntity.status(404).body(errorText);
        } else {
            return ResponseEntity.ok(user);
        }
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<?> getUserByUsername(@PathVariable String username) {
        return userService.findByUsername(username).<ResponseEntity<?>>map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found"));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUserById(@PathVariable("id") Long id) {
        UserEntity user = userService.findById(id);
        if (user == null) {
            return ResponseEntity.status(404).body(errorText);
        } else {
            userService.deleteById(id);
            publishService.publishUserDeleted(new UserEvent(user.getId(), user.getUsername(), "DELETED"));
            return ResponseEntity.ok("User deleted successfully");
        }
    }

    @PostMapping("/me")
    public ResponseEntity<UserEntity> syncUser(@RequestBody UserEntity userData, @AuthenticationPrincipal Jwt jwt) {
        String auth0Id = jwt.getClaimAsString("sub");

        if (!auth0Id.equals(userData.getAuth0Id())) {
            return ResponseEntity.status(403).build();
        }

        UserSyncResult result = userService.findOrCreateUserWithStatus(userData.getAuth0Id(), userData.getEmail(), userData.getUsername(), userData.getProfileImageUrl());

        UserEntity user = result.user();

        if (result.newlyCreated()) {
            publishService.publishUserRegistered(new UserEvent(user.getId(), user.getUsername(), user.getProfileImageUrl()));
        }

        return ResponseEntity.ok(user);
    }


    @PutMapping("/me")
    public ResponseEntity<UserEntity> updateProfile(@AuthenticationPrincipal Jwt jwt, @RequestBody UserEntity updatedUser) {
        String auth0Id = jwt.getClaimAsString("sub");
        UserEntity user = userService.updateProfile(auth0Id, updatedUser);
        publishService.publishUserUpdated(new UserEvent(user.getId(), user.getUsername(), user.getProfileImageUrl()));
        return ResponseEntity.ok(user);
    }
}
