package epam.course.appliance.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import epam.course.appliance.entity.Appliance;
import epam.course.appliance.entity.User;
import epam.course.appliance.service.ApplianceService;
import epam.course.appliance.service.UserService;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class ApplianceControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ApplianceService applianceService;

    @Mock
    private UserService userService;

    @Mock
    private epam.course.appliance.brain.service.VectorStorageService vectorStorageService;

    @InjectMocks
    private ApplianceController applianceController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(applianceController).build();
    }

    @Test
    void testCreateAppliancePageWhenModelDoesNotHaveAttributesPopulatesThem() throws Exception {
        List<User> mockUsers = Collections.singletonList(new User());
        when(userService.getAllUsers()).thenReturn(mockUsers);

        mockMvc.perform(get("/appliances/v1/create-view"))
                .andExpect(status().isOk())
                .andExpect(view().name("create_appliance"))
                .andExpect(model().attributeExists("appliance"))
                .andExpect(model().attribute("users", mockUsers));

        verify(userService, times(1)).getAllUsers();
    }

    @Test
    void testCreateAppliancePageWhenModelAlreadyHasAttributesDoesNotOverwrite() throws Exception {
        mockMvc.perform(get("/appliances/v1/create-view")
                        .flashAttr("appliance", new Appliance())
                        .flashAttr("users", Collections.singletonList(new User())))
                .andExpect(status().isOk())
                .andExpect(view().name("create_appliance"));

        verifyNoInteractions(userService);
    }

    @Test
    void testProvisionApplianceSuccess() throws Exception {
        MockMultipartFile mockFile = new MockMultipartFile(
                "manualFile", "manual.pdf", "application/pdf", "dummy content".getBytes()
        );
        User mockUser = new User();

        when(userService.getUserById("john_doe")).thenReturn(mockUser);
        when(applianceService.saveAppliance(any(Appliance.class))).thenReturn(true);
        doNothing().when(vectorStorageService).processPdfAndSave(any(MultipartFile.class), anyString(),
                anyString(), anyString());

        mockMvc.perform(multipart("/appliances/v1/create")
                        .file(mockFile)
                        .param("serialNumber", "SN12345")
                        .param("category", "Refrigerator")
                        .param("modelNumber", "MOD-99")
                        .param("modelName", "CoolFreeze")
                        .param("manufactureDate", "2026-01-01")
                        .param("warrantyExpiryDate", "2028-01-01")
                        .param("ownerUsername", "john_doe"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/appliances/v1/create-view"))
                .andExpect(flash().attribute("successMessage", "Appliance 'Refrigerator' saved successfully."));

        verify(applianceService, times(1)).saveAppliance(any(Appliance.class));
        verify(vectorStorageService, times(1))
                .processPdfAndSave(any(MultipartFile.class), anyString(), anyString(), anyString());
    }

    @Test
    void testProvisionApplianceServiceSaveFails() throws Exception {
        MockMultipartFile mockFile = new MockMultipartFile(
                "manualFile", "manual.pdf", "text/plain", "content".getBytes()
        );

        when(userService.getUserById("john_doe")).thenReturn(new User());
        when(applianceService.saveAppliance(any(Appliance.class))).thenReturn(false);
        doNothing().when(vectorStorageService)
                .processPdfAndSave(any(MultipartFile.class), anyString(), anyString(), anyString());

        mockMvc.perform(multipart("/appliances/v1/create")
                        .file(mockFile)
                        .param("serialNumber", "SN12345")
                        .param("category", "Microwave")
                        .param("modelNumber", "MOD-88")
                        .param("modelName", "WaveMaster")
                        .param("manufactureDate", "2026-01-01")
                        .param("warrantyExpiryDate", "2027-01-01")
                        .param("ownerUsername", "john_doe"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/appliances/v1/create-view"))
                .andExpect(flash().attribute("successMessage", "Failed to save appliance 'Microwave'."));
    }

    @Test
    void testProvisionApplianceExceptionThrownTriggersCatchBlock() throws Exception {
        MockMultipartFile mockFile = new MockMultipartFile(
                "manualFile", "manual.pdf", "text/plain", "content".getBytes()
        );

        mockMvc.perform(multipart("/appliances/v1/create")
                        .file(mockFile)
                        .param("serialNumber", "SN12345")
                        .param("category", "Oven")
                        .param("modelNumber", "MOD-77")
                        .param("modelName", "BakeMaster")
                        .param("manufactureDate", "invalid-date-format")
                        .param("warrantyExpiryDate", "2027-01-01")
                        .param("ownerUsername", "john_doe"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/appliances/v1/create-view"))
                .andExpect(flash().attribute("errorMessage", "Operation failed!"));

        verifyNoInteractions(applianceService);
        verifyNoInteractions(vectorStorageService);
    }

    @Test
    void testProvisionApplianceVectorStorageExceptionTriggersFailure() throws Exception {
        MockMultipartFile mockFile = new MockMultipartFile(
                "manualFile", "manual.pdf", "application/pdf", "content".getBytes()
        );
        User mockUser = new User();

        when(userService.getUserById("john_doe")).thenReturn(mockUser);
        when(applianceService.saveAppliance(any(Appliance.class))).thenReturn(true);
        doThrow(new RuntimeException("Vector storage failed"))
                .when(vectorStorageService)
                .processPdfAndSave(any(MultipartFile.class), anyString(), anyString(), anyString());

        mockMvc.perform(multipart("/appliances/v1/create")
                        .file(mockFile)
                        .param("serialNumber", "SN12345")
                        .param("category", "Dishwasher")
                        .param("modelNumber", "MOD-66")
                        .param("modelName", "WashPro")
                        .param("manufactureDate", "2026-01-01")
                        .param("warrantyExpiryDate", "2027-01-01")
                        .param("ownerUsername", "john_doe"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/appliances/v1/create-view"))
                .andExpect(flash().attribute("errorMessage", "Operation failed!"));

        verify(applianceService, times(1)).saveAppliance(any(Appliance.class));
        verify(vectorStorageService, times(1))
                .processPdfAndSave(any(MultipartFile.class), anyString(), anyString(), anyString());
    }

    @Test
    void testProvisionApplianceWhenMultipartFileIsEmptyReturnsErrorMessage() throws Exception {
        MockMultipartFile emptyFile = new MockMultipartFile(
                "manualFile", "manual.pdf", "application/pdf", new byte[0]
        );

        mockMvc.perform(multipart("/appliances/v1/create")
                        .file(emptyFile)
                        .param("serialNumber", "SN12345")
                        .param("category", "Refrigerator")
                        .param("modelNumber", "MOD-99")
                        .param("modelName", "CoolFreeze")
                        .param("manufactureDate", "2026-01-01")
                        .param("warrantyExpiryDate", "2028-01-01")
                        .param("ownerUsername", "john_doe"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/appliances/v1/create-view"))
                .andExpect(flash().attribute("errorMessage", "Manual file is required!"));

        verifyNoInteractions(userService);
        verifyNoInteractions(applianceService);
        verifyNoInteractions(vectorStorageService);
    }

    @Test
    void testRemoveAppliancePageWhenModelDoesNotHaveAppliancePopulatesIt() throws Exception {
        mockMvc.perform(get("/appliances/v1/delete-view"))
                .andExpect(status().isOk())
                .andExpect(view().name("remove_appliance"))
                .andExpect(model().attributeExists("appliance"));
    }

    @Test
    void testRemoveAppliancePageWhenModelAlreadyHasApplianceDoesNotOverwrite() throws Exception {
        mockMvc.perform(get("/appliances/v1/delete-view")
                        .flashAttr("appliance", new Appliance()))
                .andExpect(status().isOk())
                .andExpect(view().name("remove_appliance"));
    }

    @Test
    void testRemoveApplianceSuccess() throws Exception {
        doNothing().when(applianceService).removeAppliance("john_doe", "SN12345");

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/appliances/v1/delete")
                        .param("username", "john_doe")
                        .param("serialNumber", "SN12345"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"));

        verify(applianceService, times(1)).removeAppliance("john_doe", "SN12345");
    }

    @Test
    void testRemoveApplianceExceptionThrownTriggersCatchBlock() throws Exception {
        doThrow(new RuntimeException("Database error"))
                .when(applianceService).removeAppliance("john_doe", "SN12345");

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/appliances/v1/delete")
                        .param("username", "john_doe")
                        .param("serialNumber", "SN12345"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/appliances/v1/delete-view"))
                .andExpect(flash().attribute("errorMessage", "Operation failed!"));

        verify(applianceService, times(1)).removeAppliance("john_doe", "SN12345");
    }
}

