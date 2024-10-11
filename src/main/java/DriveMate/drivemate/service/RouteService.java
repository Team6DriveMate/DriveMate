package DriveMate.drivemate.service;

import DriveMate.drivemate.domain.Route;
import DriveMate.drivemate.repository.RouteRepository;
import jakarta.persistence.Table;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RouteService {
    @Autowired
    private RouteRepository routeRepository;

    @Transactional
    public void saveRoute(Route route) {
        routeRepository.save(route);
    }
}
