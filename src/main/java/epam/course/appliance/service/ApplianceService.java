package epam.course.appliance.service;

import epam.course.appliance.entity.Appliance;
import epam.course.appliance.repository.ApplianceRepository;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ApplianceService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApplianceService.class);
    
    private final ApplianceRepository applianceRepository;
    
    public ApplianceService(ApplianceRepository applianceRepository) {
        this.applianceRepository = applianceRepository;
    }

    public boolean saveAppliance(Appliance appliance) {
        try {
            applianceRepository.save(appliance);
            LOGGER.info("Appliance saved successfully");
            return true;
        } catch (Exception e) {
            LOGGER.error("Error saving appliance", e);
            return false;
        }
    }
    
    public List<Appliance> getApplianceByUsername(String id) {
        return applianceRepository.findByOwner_Username(id);
    }
}
