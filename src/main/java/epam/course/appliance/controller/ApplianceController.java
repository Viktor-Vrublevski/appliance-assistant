package epam.course.appliance.controller;

import epam.course.appliance.entity.Appliance;
import epam.course.appliance.service.ApplianceService;
import epam.course.appliance.service.UserService;
import java.time.LocalDate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/appliances/v1")
public class ApplianceController {

    private final ApplianceService applianceService;
    private final UserService userService;

    public ApplianceController(ApplianceService applianceService, UserService userService) {
        this.applianceService = applianceService;
        this.userService = userService;
    }

    @GetMapping("/create-view")
    public String createAppliancePage(Model model) {
        if (!model.containsAttribute("appliance")) {
            model.addAttribute("appliance", new Appliance());
        }
        if (!model.containsAttribute("users")) {
            model.addAttribute("users", userService.getAllUsers());
        }
        return "create_appliance";
    }

    @PostMapping("/create")
    public String provisionAppliance(@RequestParam("serialNumber") String serialNumber,
                                     @RequestParam("category") String category,
                                     @RequestParam("modelNumber") String modelNumber,
                                     @RequestParam("modelName") String modelName,
                                     @RequestParam(value = "manufactureDate", required = false) String manufactureDateStr,
                                     @RequestParam(value = "warrantyExpiryDate", required = false) String warrantyExpiryStr,
                                     @RequestParam("ownerUsername") String username,
                                     @RequestParam("manualFile") MultipartFile manualFile,
                                     RedirectAttributes redirectAttributes) {
        try {
            Appliance appliance = new Appliance();
            appliance.setSerialNumber(serialNumber);
            appliance.setCategory(category);
            appliance.setModelNumber(modelNumber);
            appliance.setModelName(modelName);
            appliance.setManufactureDate(LocalDate.parse(manufactureDateStr));
            appliance.setWarrantyExpiryDate(LocalDate.parse(warrantyExpiryStr));
            appliance.setOwner(userService.getUserById(username));
            boolean successMessage = applianceService.saveAppliance(appliance);
            redirectAttributes.addFlashAttribute("successMessage",
                    successMessage ? String.format("Appliance '%s' saved successfully.", category)
                            : String.format("Failed to save appliance '%s'.", category));
            return "redirect:/create_appliance";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("successMessage", "Operation failed!");
            return "redirect:/create_appliance";
        }
    }
}
