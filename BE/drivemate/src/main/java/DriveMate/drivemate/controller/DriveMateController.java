package DriveMate.drivemate.controller;

import DriveMate.drivemate.domain.Route;
import DriveMate.drivemate.service.DriveMateService;
import DriveMate.drivemate.service.RouteService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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

    @GetMapping("/traffic")
    public String getTraffic(@RequestParam double centerLat, @RequestParam double centerLon){
        return driveMateService.getTraffic(centerLat, centerLon);
    }

    @GetMapping("/address")
    public String getAddress(@RequestParam String address){
        return driveMateService.addressToCoordinate(address);
    }

    @PostMapping("/create")
    public ResponseEntity<Route> createRoute(@RequestParam double startY, @RequestParam double startX,
                                             @RequestParam double endY, @RequestParam double endX) {

        // TMAP API로부터 경로 데이터를 String으로 받아옴
        String tmapResponse = driveMateService.getRoute(startY, startX, endY, endX);

        try {
            // ObjectMapper를 사용하여 String을 JsonNode로 변환
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(tmapResponse);

            // 변환된 JsonNode를 parseRouteData에 전달
            Route route = driveMateService.parseRouteData(jsonNode);
            routeService.saveRoute(route);

            return ResponseEntity.ok(route);
        } catch (Exception e) {
            // 오류가 발생하면 에러 처리
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

}
