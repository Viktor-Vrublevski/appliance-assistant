package epam.course.appliance.repository;

import epam.course.appliance.entity.Appliance;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApplianceRepository extends JpaRepository<Appliance, String> {
    List<Appliance> findByOwner_Username(String username);

    void deleteByOwner_UsernameAndSerialNumber(String username, String serialNumber);
}
