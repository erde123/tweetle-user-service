package nl.fontys.tweetleuserservice.controller;

import nl.fontys.tweetleuserservice.business.service.UserService;
import nl.fontys.tweetleuserservice.persistence.entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = {"http://localhost:3000"})
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<List<UserEntity>> getAllUsers() {
        return ResponseEntity.ok(userService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getUserById(@PathVariable("id") Long id) {
        UserEntity user = userService.findById(id);
        if (user == null) {
            return ResponseEntity.status(404).body("User not found");
        } else {
            return ResponseEntity.ok(user);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUserById(@PathVariable("id") Long id) {
        UserEntity user = userService.findById(id);
        if (user == null) {
            return ResponseEntity.status(404).body("User not found");
        } else {
            userService.deleteById(id);
            return ResponseEntity.ok("User deleted successfully");
        }
    }

    @GetMapping("/me")
    public ResponseEntity<UserEntity> getCurrentUser(@AuthenticationPrincipal Jwt jwt) {
        String auth0Id = jwt.getClaimAsString("sub");
        String email = jwt.getClaimAsString("email");
        String username = jwt.getClaimAsString("nickname");

        UserEntity user = userService.findOrCreateUser(auth0Id, email, username);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/me")
    public ResponseEntity<UserEntity> updateProfile(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody UserEntity updatedUser) {
        String auth0Id = jwt.getClaimAsString("sub");
        UserEntity user = userService.updateProfile(auth0Id, updatedUser);
        return ResponseEntity.ok(user);
    }
}
