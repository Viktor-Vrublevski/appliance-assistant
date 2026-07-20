package epam.course.appliance.controller;

import epam.course.appliance.common.UserContextHolder;
import epam.course.appliance.dto.UserChatData;
import epam.course.appliance.entity.User;
import epam.course.appliance.service.UserService;
import jakarta.servlet.http.HttpSession;
import java.util.UUID;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class MainController {

    private final UserService userService;

    public MainController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/main")
    public String provisioningPage() {
        return "index";
    }

    @GetMapping("/login-view")
    public String loginPage() {
        return "login";
    }

    @PostMapping("/verify-user")
    public String verifyUserExists(
            @RequestParam("username") String username,
            RedirectAttributes redirectAttributes,
            HttpSession session) {
        User user = userService.getUserById(username);
        if (user == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "there is not such username");
            return "redirect:/login-view";
        }
        UserChatData userChatData = new UserChatData();
        userChatData.setUsername(username);
        userChatData.setConversationId(UserContextHolder.getUserChatData(session.getId()) == null ?
                UUID.randomUUID().toString() : UserContextHolder.getUserChatData(session.getId()).getConversationId());
        UserContextHolder.setUserChatData(session.getId(), userChatData);
        return "redirect:/chat-view";
    }
}