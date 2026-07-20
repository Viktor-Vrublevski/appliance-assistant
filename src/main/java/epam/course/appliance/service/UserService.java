package epam.course.appliance.service;

import epam.course.appliance.entity.User;
import epam.course.appliance.repository.UserRepository;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);
    
    private final UserRepository userRepository;
    
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean saveUser(User user) {
        try {
            if (userRepository.existsById(user.getUsername())) {
                LOGGER.info("User with username '{}' already exists", user.getUsername());
                return false;
            }
            userRepository.save(user);
            LOGGER.info("User saved successfully");
            return true;
        } catch (Exception e) {
            LOGGER.error("Error saving user", e);
            return false;
        }
    }
    
    public User getUserById(String id) {
        return userRepository.findById(id).orElse(null);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public void deleteUser(String username) {
        if (userRepository.existsById(username)) {
        userRepository.deleteById(username);
        } else {
            LOGGER.error("User with username '{}' does not exist", username);
            throw new IllegalArgumentException("User with username '" + username + "' does not exist");
        }
    }
}
