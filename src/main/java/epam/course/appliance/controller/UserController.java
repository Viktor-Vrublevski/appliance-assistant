package epam.course.appliance.controller;

import epam.course.appliance.entity.User;
import epam.course.appliance.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/users/v1")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/create-view")
    public String createUserPage(Model model) {
        if (!model.containsAttribute("user")) {
            model.addAttribute("user", new User());
        }
        return "create_user";
    }
    @PostMapping("/create")
    public String createUser(@RequestParam("username") String username,
                             @RequestParam("userName") String userName,
                             @RequestParam("userAddress") String userAddress,
                             Model model) {
        try {
            User user = new User();
            user.setUsername(username);
            user.setUserFullName(userName);
            user.setAddress(userAddress);
            boolean isSaved = userService.saveUser(user);
            if (isSaved) {
                model.addAttribute("successMessage", String.format("User '%s' saved successfully.", userName));
            } else {
                model.addAttribute("errorMessage", String.format("Failed to save user '%s' the username already exists.", username));
            }
            return "create_user";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "An error occurred while saving the user.");
            return "create_user";
        }
    }

    @GetMapping("/delete-view")
    public String removeUserPage(Model model) {
        if (!model.containsAttribute("user")) {
            model.addAttribute("user", new User());
        }
        return "remove_user";
    }

    @PostMapping("/delete")
    public String deleteUser(@RequestParam("username") String username,
                             RedirectAttributes redirectAttributes) {
        try {
        userService.deleteUser(username);
        return "index";
        } catch (Exception e) {
            redirectAttributes
                    .addFlashAttribute("errorMessage",
                            "An error occurred while deleting the user.");
            return "redirect:/users/v1/delete-view";
        }
    }
}