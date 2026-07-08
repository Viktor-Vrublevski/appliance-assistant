package epam.course.appliance.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import epam.course.appliance.entity.User;
import epam.course.appliance.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUsername("jdoe");
        user.setUserFullName("John Doe");
        user.setAddress("1 Main St");
    }

    @Test
    void saveUserReturnsTrueWhenRepositorySucceeds() {
        when(userRepository.save(user)).thenReturn(user);

        boolean result = userService.saveUser(user);

        assertTrue(result);
        verify(userRepository).save(user);
    }

    @Test
    void saveUserReturnsFalseWhenRepositoryThrows() {
        doThrow(new RuntimeException("db down")).when(userRepository).save(any(User.class));

        boolean result = userService.saveUser(user);

        assertFalse(result);
        verify(userRepository).save(user);
    }

    @Test
    void getUserByIdReturnsUserWhenFound() {
        when(userRepository.findById("jdoe")).thenReturn(Optional.of(user));

        User result = userService.getUserById("jdoe");

        assertSame(user, result);
        verify(userRepository).findById("jdoe");
    }

    @Test
    void getUserByIdReturnsNullWhenNotFound() {
        when(userRepository.findById("missing")).thenReturn(Optional.empty());

        User result = userService.getUserById("missing");

        assertNull(result);
        verify(userRepository).findById("missing");
    }

    @Test
    void getAllUsersReturnsUsersFromRepository() {
        List<User> users = List.of(user);
        when(userRepository.findAll()).thenReturn(users);

        List<User> result = userService.getAllUsers();

        assertEquals(users, result);
        verify(userRepository).findAll();
    }
}