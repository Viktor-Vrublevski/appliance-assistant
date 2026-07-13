package epam.course.appliance.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import epam.course.appliance.entity.Appliance;
import epam.course.appliance.repository.ApplianceRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ApplianceServiceTest {

    @Mock
    private ApplianceRepository applianceRepository;

    @InjectMocks
    private ApplianceService applianceService;

    private Appliance appliance;

    @BeforeEach
    void setUp() {
        appliance = new Appliance();
        appliance.setSerialNumber("SN-123");
        appliance.setCategory("Refrigerator");
        appliance.setModelNumber("MN-456");
        appliance.setModelName("CoolMax");
    }

    @Test
    void saveApplianceReturnsTrueWhenRepositorySucceeds() {
        when(applianceRepository.save(appliance)).thenReturn(appliance);

        boolean result = applianceService.saveAppliance(appliance);

        assertTrue(result);
        verify(applianceRepository).save(appliance);
    }

    @Test
    void saveApplianceReturnsFalseWhenRepositoryThrows() {
        doThrow(new RuntimeException("db down")).when(applianceRepository).save(any(Appliance.class));

        boolean result = applianceService.saveAppliance(appliance);

        assertFalse(result);
        verify(applianceRepository).save(appliance);
    }

    @Test
    void getApplianceByIdReturnsApplianceWhenFound() {
        when(applianceRepository.findByOwner_Username("SN-123")).thenReturn(List.of(appliance));

        List<Appliance> result = applianceService.getApplianceByUsername("SN-123");

        assertSame(appliance, result.getFirst());
        verify(applianceRepository).findByOwner_Username("SN-123");
    }

    @Test
    void getApplianceByUsernameReturnsNullWhenNotFound() {
        when(applianceRepository.findByOwner_Username("missing")).thenReturn(List.of());

        List<Appliance> result = applianceService.getApplianceByUsername("missing");

        assertTrue(result.isEmpty());
        verify(applianceRepository).findByOwner_Username("missing");
    }
}