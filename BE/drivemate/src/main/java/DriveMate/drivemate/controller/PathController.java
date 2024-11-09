package DriveMate.drivemate.controller;

import DriveMate.drivemate.DTO.*;
import DriveMate.drivemate.dataclass.Location;
import DriveMate.drivemate.domain.*;
import DriveMate.drivemate.service.DriveMateService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/path")
public class PathController {

    private final DriveMateService driveMateService;

    @Autowired
    public PathController(DriveMateService driveMateService){
        this.driveMateService = driveMateService;
    }

    @PostMapping("/set")
    public RouteResponseDTO setRoute(@RequestBody RouteRequestDTO routeRequestDTO){
        String start = routeRequestDTO.getStartLocation().getName();
        String end = routeRequestDTO.getEndLocation().getName();

        Location startLocation = driveMateService.addressToCoordinate(start);
        Location endLocation = driveMateService.addressToCoordinate(end);

        System.out.println(startLocation.getLatitude() + " " + startLocation.getLongitude());
        System.out.println(endLocation.getLatitude() + " " + endLocation.getLongitude());

        String tmapResponse = driveMateService.getRoute(
                startLocation.getLatitude(), startLocation.getLongitude(),
                endLocation.getLatitude(), endLocation.getLongitude()
        );

        try {
            // ObjectMapper를 사용하여 String을 JsonNode로 변환
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(tmapResponse);

            // 변환된 JsonNode를 parseRouteData에 전달
            Route route = driveMateService.parseRouteData(jsonNode);
            driveMateService.setRouteTmp(route);

            RouteResponseDTO routeResponseDTO = new RouteResponseDTO();
            routeResponseDTO.setTotalTime(route.getTotalTime());
            routeResponseDTO.setTotalDistance(route.getTotalDistance());

            RouteDTO routeDTO = new RouteDTO();
            for (SemiRoute semiRoute : route.getSemiRouteList()){
                if (semiRoute instanceof SemiRouteLineString){
                    SegmentDTO segmentDTO = new SegmentDTO();
                    List<Coordinate> coordinateList = semiRoute.getCoordinateList();

                    PointDTO startPoint = new PointDTO();
                    startPoint.setLat(coordinateList.get(0).getFirst());
                    startPoint.setLng(coordinateList.get(0).getSecond());
                    startPoint.setName("");
                    segmentDTO.setStartPoint(startPoint);

                    PointDTO endPoint = new PointDTO();
                    endPoint.setLat(coordinateList.get(coordinateList.size()-1).getFirst());
                    endPoint.setLng(coordinateList.get(coordinateList.size()-1).getSecond());
                    endPoint.setName("");
                    segmentDTO.setEndPoint(endPoint);

                    for (Coordinate coordinate : coordinateList) {
                        PathPointDTO pathPoint = new PathPointDTO();
                        pathPoint.setLat(coordinate.getFirst());
                        pathPoint.setLng(coordinate.getSecond());
                        segmentDTO.addPathPointDTO(pathPoint);
                    }

                    segmentDTO.setTime(((SemiRouteLineString) semiRoute).getTime());
                    segmentDTO.setDistance(((SemiRouteLineString) semiRoute).getDistance());
                    segmentDTO.setRoadName(((SemiRouteLineString) semiRoute).getName());
                    System.out.println(semiRoute.getNumIndex());
                    segmentDTO.setTraffic(semiRoute.getCoordinateList().get(0).getSemiRouteRoadInfo().getCongestion());
                    routeDTO.addSegment(segmentDTO);
                }
            }
            routeResponseDTO.setRoute(routeDTO);

            System.out.println(routeResponseDTO.getRoute().getSegments().get(0).getTime());
            return routeResponseDTO;

        } catch (Exception e) {
            // 오류가 발생하면 에러 처리
            e.getStackTrace();
            return null;
        }

    }
}
