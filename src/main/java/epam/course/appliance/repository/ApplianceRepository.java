package epam.course.appliance.repository;

import epam.course.appliance.entity.Appliance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApplianceRepository extends JpaRepository<Appliance, String> {
}
