package nl.fontys.tweetleuserservice.business.service;

import jakarta.transaction.Transactional;
import nl.fontys.tweetleuserservice.domain.UserSyncResult;
import nl.fontys.tweetleuserservice.persistence.entity.UserEntity;
import nl.fontys.tweetleuserservice.persistence.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private Auth0Service auth0Service;

    @Transactional
    public UserSyncResult findOrCreateUserWithStatus(String auth0Id, String email, String username, String picture) {
        return userRepository.findByAuth0Id(auth0Id)
                .map(existing -> new UserSyncResult(existing, false))
                .orElseGet(() -> {
                    UserEntity newUser = UserEntity.builder()
                            .auth0Id(auth0Id)
                            .email(email)
                            .username(username)
                            .profileImageUrl(picture)
                            .createdAt(new Date())
                            .build();

                    try {
                        UserEntity saved = userRepository.save(newUser);
                        return new UserSyncResult(saved, true);
                    } catch (DataIntegrityViolationException e) {
                        // Another request created the same user concurrently
                        UserEntity existing = userRepository.findByAuth0Id(auth0Id).orElseThrow();
                        return new UserSyncResult(existing, false);
                    }
                });
    }

    @Transactional
    public UserEntity findOrCreateUser(String auth0Id, String email, String username, String picture) {
        return findOrCreateUserWithStatus(auth0Id, email, username, picture).user();
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

    @Transactional
    public void deleteById (Long id) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        auth0Service.deleteAuth0User(user.getAuth0Id());

        userRepository.deleteById(id);
    }
}
