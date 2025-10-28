package nl.fontys.tweetleuserservice.business.service;

import nl.fontys.tweetleuserservice.persistence.entity.UserEntity;
import nl.fontys.tweetleuserservice.persistence.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private UserEntity testUser;

    @BeforeEach
    void setUp() {
        testUser = UserEntity.builder()
                .id(1L)
                .auth0Id("auth0|12345")
                .email("test@example.com")
                .username("testUser")
                .profileImageUrl("http://example.com/pic.jpg")
                .bio("Hello world")
                .createdAt(new Date())
                .build();
    }

    // --- findOrCreateUser() ---
    @Test
    void findOrCreateUser_WhenUserExists_ShouldReturnExistingUser() {
        when(userRepository.findByAuth0Id("auth0|12345"))
                .thenReturn(Optional.of(testUser));

        UserEntity result = userService.findOrCreateUser(
                "auth0|12345", "test@example.com", "testUser", "http://example.com/pic.jpg"
        );

        assertEquals(testUser, result);
        verify(userRepository, times(1)).findByAuth0Id("auth0|12345");
        verify(userRepository, never()).save(any(UserEntity.class));
    }

    @Test
    void findOrCreateUser_WhenUserDoesNotExist_ShouldCreateAndSaveNewUser() {
        when(userRepository.findByAuth0Id("auth0|new")).thenReturn(Optional.empty());
        when(userRepository.save(any(UserEntity.class))).thenReturn(testUser);

        UserEntity result = userService.findOrCreateUser(
                "auth0|new", "new@example.com", "newUser", "http://example.com/new.jpg"
        );

        assertNotNull(result);
        verify(userRepository, times(1)).findByAuth0Id("auth0|new");
        verify(userRepository, times(1)).save(any(UserEntity.class));
    }

    // --- updateProfile() ---
    @Test
    void updateProfile_WhenUserExists_ShouldUpdateFieldsAndSave() {
        when(userRepository.findByAuth0Id("auth0|12345")).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(UserEntity.class))).thenReturn(testUser);

        UserEntity updatedData = new UserEntity();
        updatedData.setBio("Updated bio");
        updatedData.setUsername("updatedUser");
        updatedData.setProfileImageUrl("http://example.com/new.jpg");

        UserEntity result = userService.updateProfile("auth0|12345", updatedData);

        assertEquals("Updated bio", result.getBio());
        assertEquals("updatedUser", result.getUsername());
        assertEquals("http://example.com/new.jpg", result.getProfileImageUrl());
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    void updateProfile_WhenUserNotFound_ShouldThrowException() {
        when(userRepository.findByAuth0Id("auth0|notfound")).thenReturn(Optional.empty());

        UserEntity updatedData = new UserEntity();

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                userService.updateProfile("auth0|notfound", updatedData)
        );

        assertEquals("User not found", ex.getMessage());
        verify(userRepository, never()).save(any(UserEntity.class));
    }

    // --- save() ---
    @Test
    void save_ShouldCallRepositorySave() {
        when(userRepository.save(testUser)).thenReturn(testUser);

        userService.save(testUser);

        verify(userRepository, times(1)).save(testUser);
    }

    // --- findAll() ---
    @Test
    void findAll_ShouldReturnListOfUsers() {
        List<UserEntity> expectedUsers = Arrays.asList(testUser);
        when(userRepository.findAll()).thenReturn(expectedUsers);

        List<UserEntity> result = userService.findAll();

        assertEquals(expectedUsers, result);
        verify(userRepository, times(1)).findAll();
    }

    // --- findById() ---
    @Test
    void findById_WhenExists_ShouldReturnUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        UserEntity result = userService.findById(1L);

        assertEquals(testUser, result);
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void findById_WhenNotExists_ShouldReturnNull() {
        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        UserEntity result = userService.findById(2L);

        assertNull(result);
        verify(userRepository, times(1)).findById(2L);
    }

    // --- findByAuth0Id() ---
    @Test
    void findByAuth0Id_WhenExists_ShouldReturnUser() {
        when(userRepository.findByAuth0Id("auth0|12345")).thenReturn(Optional.of(testUser));

        UserEntity result = userService.findByAuth0Id("auth0|12345");

        assertEquals(testUser, result);
        verify(userRepository, times(1)).findByAuth0Id("auth0|12345");
    }

    @Test
    void findByAuth0Id_WhenNotExists_ShouldReturnNull() {
        when(userRepository.findByAuth0Id("auth0|missing")).thenReturn(Optional.empty());

        UserEntity result = userService.findByAuth0Id("auth0|missing");

        assertNull(result);
        verify(userRepository, times(1)).findByAuth0Id("auth0|missing");
    }

    // --- deleteById() ---
    @Test
    void deleteById_ShouldCallRepositoryDeleteById() {
        doNothing().when(userRepository).deleteById(1L);

        userService.deleteById(1L);

        verify(userRepository, times(1)).deleteById(1L);
    }
}
