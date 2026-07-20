package epam.course.appliance.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import epam.course.appliance.entity.User;
import epam.course.appliance.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    void testCreateUserPageWhenUserAttributeNotPresentAddsNewUserToModel() throws Exception {
        mockMvc.perform(get("/users/v1/create-view"))
                .andExpect(status().isOk())
                .andExpect(view().name("create_user"))
                .andExpect(model().attributeExists("user"));
    }

    @Test
    void testCreateUserPageWhenUserAttributeAlreadyPresentDoesNotOverwrite() throws Exception {
        User existingUser = new User();
        existingUser.setUsername("existing_user");

        mockMvc.perform(get("/users/v1/create-view")
                        .flashAttr("user", existingUser))
                .andExpect(status().isOk())
                .andExpect(view().name("create_user"))
                .andExpect(model().attribute("user", existingUser));
    }


    @Test
    void testCreateUserSuccess() throws Exception {
        when(userService.saveUser(any(User.class))).thenReturn(true);

        mockMvc.perform(post("/users/v1/create")
                        .param("username", "johndoe")
                        .param("userName", "John Doe")
                        .param("userAddress", "123 Main St"))
                .andExpect(status().isOk())
                .andExpect(view().name("create_user"))
                .andExpect(model().attribute("successMessage", "User 'John Doe' saved successfully."));

        verify(userService, times(1)).saveUser(any(User.class));
    }

    @Test
    void testCreateUserServiceSaveFails() throws Exception {
        when(userService.saveUser(any(User.class))).thenReturn(false);

        mockMvc.perform(post("/users/v1/create")
                        .param("username", "johndoe")
                        .param("userName", "John Doe")
                        .param("userAddress", "123 Main St"))
                .andExpect(status().isOk())
                .andExpect(view().name("create_user"))
                .andExpect(model().attribute("errorMessage", "Failed to save user 'johndoe' the username already exists."));
    }

    @Test
    void testCreateUserExceptionThrownTriggersCatchBlock() throws Exception {
        when(userService.saveUser(any(User.class))).thenThrow(new RuntimeException("Database down"));

        mockMvc.perform(post("/users/v1/create")
                        .param("username", "johndoe")
                        .param("userName", "John Doe")
                        .param("userAddress", "123 Main St"))
                .andExpect(status().isOk())
                .andExpect(view().name("create_user"))
                .andExpect(model().attribute("errorMessage", "An error occurred while saving the user."));
    }

    @Test
    void testRemoveUserPageWhenUserAttributeNotPresentAddsNewUserToModel() throws Exception {
        mockMvc.perform(get("/users/v1/delete-view"))
                .andExpect(status().isOk())
                .andExpect(view().name("remove_user"))
                .andExpect(model().attributeExists("user"));
    }

    @Test
    void testRemoveUserPageWhenUserAttributeAlreadyPresentDoesNotOverwrite() throws Exception {
        User existingUser = new User();
        existingUser.setUsername("existing_user");

        mockMvc.perform(get("/users/v1/delete-view")
                        .flashAttr("user", existingUser))
                .andExpect(status().isOk())
                .andExpect(view().name("remove_user"))
                .andExpect(model().attribute("user", existingUser));
    }

    @Test
    void testDeleteUserSuccess() throws Exception {
        org.mockito.Mockito.doNothing().when(userService).deleteUser("johndoe");

        mockMvc.perform(post("/users/v1/delete")
                        .param("username", "johndoe"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"));

        verify(userService, times(1)).deleteUser("johndoe");
    }

    @Test
    void testDeleteUserExceptionThrownTriggersCatchBlock() throws Exception {
        org.mockito.Mockito.doThrow(new RuntimeException("User not found"))
                .when(userService).deleteUser("johndoe");

        mockMvc.perform(post("/users/v1/delete")
                        .param("username", "johndoe"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users/v1/delete-view"))
                .andExpect(flash().attribute("errorMessage", "An error occurred while deleting the user."));

        verify(userService, times(1)).deleteUser("johndoe");
    }
}
