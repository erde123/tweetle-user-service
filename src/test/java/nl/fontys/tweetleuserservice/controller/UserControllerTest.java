package nl.fontys.tweetleuserservice.controller;

import nl.fontys.tweetleuserservice.business.service.UserService;
import nl.fontys.tweetleuserservice.persistence.entity.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private UserEntity testUser;

    @BeforeEach
    void setUp() {
        testUser = new UserEntity();
        testUser.setId(1L);
        testUser.setAuth0Id("auth0|12345");
        testUser.setEmail("test@example.com");
        testUser.setUsername("testUser");
        testUser.setProfileImageUrl("http://example.com/image.png");
    }

    // GET /api/users
    @Test
    void getAllUsers_WhenSuccessful_ShouldReturnListOfUsers() {
        when(userService.findAll()).thenReturn(Arrays.asList(testUser));

        ResponseEntity<List<UserEntity>> response = userController.getAllUsers();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals(testUser, response.getBody().get(0));
        verify(userService, times(1)).findAll();
    }

    @Test
    void getAllUsers_WhenExceptionThrown_ShouldReturnError() {
        when(userService.findAll()).thenThrow(new RuntimeException("Test error"));

        assertThrows(RuntimeException.class, () -> userController.getAllUsers());
        verify(userService, times(1)).findAll();
    }

    // GET /id/{id}
    @Test
    void getUserById_WhenUserExists_ShouldReturnUser() {
        when(userService.findById(1L)).thenReturn(testUser);

        ResponseEntity<Object> response = userController.getUserById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testUser, response.getBody());
        verify(userService, times(1)).findById(1L);
    }

    @Test
    void getUserById_WhenUserNotFound_ShouldReturn404() {
        when(userService.findById(99L)).thenReturn(null);

        ResponseEntity<Object> response = userController.getUserById(99L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("User not found", response.getBody());
        verify(userService, times(1)).findById(99L);
    }

    // GET /auth0/{auth0Id}
    @Test
    void getUserByAuth0Id_WhenUserExists_ShouldReturnUser() {
        when(userService.findByAuth0Id("auth0|12345")).thenReturn(testUser);

        ResponseEntity<Object> response = userController.getUserByAuth0Id("auth0|12345");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testUser, response.getBody());
        verify(userService, times(1)).findByAuth0Id("auth0|12345");
    }

    @Test
    void getUserByAuth0Id_WhenUserNotFound_ShouldReturn404() {
        when(userService.findByAuth0Id("auth0|notfound")).thenReturn(null);

        ResponseEntity<Object> response = userController.getUserByAuth0Id("auth0|notfound");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("User not found", response.getBody());
        verify(userService, times(1)).findByAuth0Id("auth0|notfound");
    }

    // DELETE /{id}
    @Test
    void deleteUserById_WhenUserExists_ShouldReturnOk() {
        when(userService.findById(1L)).thenReturn(testUser);
        doNothing().when(userService).deleteById(1L);

        ResponseEntity<String> response = userController.deleteUserById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("User deleted successfully", response.getBody());
        verify(userService, times(1)).deleteById(1L);
    }

    @Test
    void deleteUserById_WhenUserNotFound_ShouldReturn404() {
        when(userService.findById(99L)).thenReturn(null);

        ResponseEntity<String> response = userController.deleteUserById(99L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("User not found", response.getBody());
        verify(userService, never()).deleteById(anyLong());
    }

    // POST /me
    @Test
    void syncUser_WhenAuth0Matches_ShouldReturnUser() {
        Jwt jwt = mock(Jwt.class);
        when(jwt.getClaimAsString("sub")).thenReturn("auth0|12345");
        when(userService.findOrCreateUser(anyString(), anyString(), anyString(), anyString()))
                .thenReturn(testUser);

        ResponseEntity<UserEntity> response = userController.syncUser(testUser, jwt);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testUser, response.getBody());
        verify(userService, times(1))
                .findOrCreateUser(testUser.getAuth0Id(), testUser.getEmail(),
                        testUser.getUsername(), testUser.getProfileImageUrl());
    }

    @Test
    void syncUser_WhenAuth0Mismatch_ShouldReturn403() {
        Jwt jwt = mock(Jwt.class);
        when(jwt.getClaimAsString("sub")).thenReturn("auth0|different");

        ResponseEntity<UserEntity> response = userController.syncUser(testUser, jwt);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        verify(userService, never()).findOrCreateUser(anyString(), anyString(), anyString(), anyString());
    }

    // PUT /me
    @Test
    void updateProfile_WhenSuccessful_ShouldReturnUpdatedUser() {
        Jwt jwt = mock(Jwt.class);
        when(jwt.getClaimAsString("sub")).thenReturn("auth0|12345");
        when(userService.updateProfile(eq("auth0|12345"), any(UserEntity.class)))
                .thenReturn(testUser);

        ResponseEntity<UserEntity> response = userController.updateProfile(jwt, testUser);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testUser, response.getBody());
        verify(userService, times(1))
                .updateProfile(eq("auth0|12345"), eq(testUser));
    }
}
