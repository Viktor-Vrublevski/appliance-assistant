package epam.course.appliance.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ChatController {

    @GetMapping("/chat-view")
    public String chatPage(Model model) {
        return "chat";
    }
}
