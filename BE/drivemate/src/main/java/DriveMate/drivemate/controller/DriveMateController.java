package DriveMate.drivemate.controller;

import DriveMate.drivemate.dataclass.Location;
import DriveMate.drivemate.domain.Coordinate;
import DriveMate.drivemate.domain.Route;
import DriveMate.drivemate.domain.SemiRoute;
import DriveMate.drivemate.service.DriveMateService;
import DriveMate.drivemate.service.RouteService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// Modification for testing

@RestController
@RequestMapping("/DriveMate")
public class DriveMateController {
    @Autowired
    private DriveMateService driveMateService;
    @Autowired
    private RouteService routeService;

    @PostMapping("/route")
    public String getRoute(@RequestParam double startY, @RequestParam double startX,
                           @RequestParam double endY, @RequestParam double endX) {
        return driveMateService.getRoute(startY, startX, endY, endX);
    }

    /*
    @GetMapping("/traffic")
    public String getTraffic(@RequestParam double centerLat, @RequestParam double centerLon){
        return driveMateService.getTraffic(centerLat, centerLon);
    }
    */

    @GetMapping("/address")
    public Location getAddress(@RequestParam String address){
        return driveMateService.addressToCoordinate(address);
    }

    @PostMapping("/create")
    public ResponseEntity<Route> createRoute(@RequestParam double startY, @RequestParam double startX,
                                             @RequestParam double endY, @RequestParam double endX) {

        // TMAP API로부터 경로 데이터를 String으로 받아옴
        String tmapResponse = driveMateService.getRoute(startY, startX, endY, endX);

        System.out.println(tmapResponse);

        int batchSize = 30;

        try {
            // ObjectMapper를 사용하여 String을 JsonNode로 변환
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(tmapResponse);
            // 변환된 JsonNode를 parseRouteData에 전달
            Route route = driveMateService.parseRouteData(jsonNode);
            driveMateService.setRouteTmp(route);
            System.out.println(route.getTotalDistance());
            driveMateService.saveRoute(route);

            return ResponseEntity.ok(route);
        } catch (Exception e) {
            // 오류가 발생하면 에러 처리
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/test")
    public String Test(){
        Route route = routeService.getRoute().get(0);
        Coordinate coordinate = route.getSemiRouteList().get(2).getCoordinateList().get(0);
        SemiRoute semiRoute = route.getSemiRouteList().get(1);
        if(semiRoute.getClass().getName().equals("DriveMate.drivemate.domain.SemiRoutePoint")){
            System.out.println("yes");
        }
        else {
            System.out.println(semiRoute.getClass().getName());
        }

        String trafficRespond = driveMateService.getTraffic(coordinate);
        Coordinate rst = new Coordinate();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(trafficRespond);
            driveMateService.parseTrafficInfo(jsonNode, coordinate);
        }
        catch (Exception e) {
            // 오류가 발생하면 에러 처리
        }
        return coordinate.getSemiRouteRoadInfo().getDescription();
    }

}
