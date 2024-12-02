package DriveMate.drivemate.repository;

import DriveMate.drivemate.domain.Route;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SemiRouteRepository extends JpaRepository<Route, Long> {
}