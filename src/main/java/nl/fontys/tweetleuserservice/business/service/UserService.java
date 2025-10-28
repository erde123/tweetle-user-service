package nl.fontys.tweetleuserservice.business.service;

import nl.fontys.tweetleuserservice.domain.User;
import nl.fontys.tweetleuserservice.persistence.entity.UserEntity;
import nl.fontys.tweetleuserservice.persistence.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public UserEntity findOrCreateUser(String auth0Id, String email, String username, String picture) {
        return userRepository.findByAuth0Id(auth0Id).orElseGet(() -> {
            UserEntity newUser = UserEntity.builder()
                    .auth0Id(auth0Id)
                    .email(email)
                    .username(username)
                    .profileImageUrl(picture)
                    .createdAt(new Date())
                    .build();
            return userRepository.save(newUser);
        });
    }


    public UserEntity updateProfile(String auth0Id, UserEntity updatedData) {
        UserEntity user = userRepository.findByAuth0Id(auth0Id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setBio(updatedData.getBio());
        user.setProfileImageUrl(updatedData.getProfileImageUrl());
        user.setUsername(updatedData.getUsername());
        return userRepository.save(user);
    }


    public void save(UserEntity user) { userRepository.save(user); }

    public List<UserEntity> findAll() { return userRepository.findAll(); }

    public UserEntity findById (Long id) { return userRepository.findById(id).orElse(null); }

    public UserEntity findByAuth0Id (String auth0Id) { return userRepository.findByAuth0Id(auth0Id).orElse(null); }


    public void deleteById (Long id) { userRepository.deleteById(id); }
}
