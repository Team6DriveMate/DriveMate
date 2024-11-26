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

    @PostMapping("/coord")
    public AddressRespondDTO addressToCoord(@RequestBody AddressRequestDTO addressRequestDTO){
        String address = addressRequestDTO.getAddress();
        Location location = driveMateService.addressToCoordinate(address);
        AddressRespondDTO addressRespondDTO = new AddressRespondDTO();
        addressRespondDTO.setLat(location.getLatitude());
        addressRespondDTO.setLng(location.getLongitude());
        return addressRespondDTO;
    }

    @PostMapping("/set")
    public RouteResponseDTO setRoute(@RequestBody RouteRequestDTO routeRequestDTO){

        String tmapResponse = driveMateService.getRoute(
                routeRequestDTO.getStart_location().getLat(), routeRequestDTO.getStart_location().getLng(),
                routeRequestDTO.getEnd_location().getLat(), routeRequestDTO.getEnd_location().getLng()
        );

        try {
            // ObjectMapper를 사용하여 String을 JsonNode로 변환
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(tmapResponse);

            // 변환된 JsonNode를 parseRouteData에 전달
            Route route = driveMateService.parseRouteData(jsonNode);
            driveMateService.setPreRouteTmp(route);

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
                    segmentDTO.setStartPoint(startPoint);

                    PointDTO endPoint = new PointDTO();
                    endPoint.setLat(coordinateList.get(coordinateList.size()-1).getFirst());
                    endPoint.setLng(coordinateList.get(coordinateList.size()-1).getSecond());
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
                    segmentDTO.setSegmentIndex(semiRoute.getNumIndex());
                    switch (((SemiRouteLineString) semiRoute).getRoadType()){
                        case 0:
                            segmentDTO.setRoadType("고속국도");
                            break;
                        case 1:
                            segmentDTO.setRoadType("자동차전용");
                            break;
                        case 2:
                            segmentDTO.setRoadType("국도");
                            break;
                        case 3:
                            segmentDTO.setRoadType("국가지원 지방도");
                            break;
                        case 4:
                            segmentDTO.setRoadType("지방도");
                            break;
                        case 5:
                            segmentDTO.setRoadType("5-6차선");
                            break;
                        case 6:
                            segmentDTO.setRoadType("3-4차선");
                            break;
                        case 7:
                            segmentDTO.setRoadType("2차선");
                            break;
                        case 8:
                            segmentDTO.setRoadType("1차선");
                            break;
                        case 9:
                            segmentDTO.setRoadType("이면도로");
                            break;
                        case 10:
                            segmentDTO.setRoadType("페리항로");
                            break;
                        case 11:
                            segmentDTO.setRoadType("아파트 단지 내 도로");
                            break;
                        case 12:
                            segmentDTO.setRoadType("시장 내 도로");
                            break;
                        case 16:
                            segmentDTO.setRoadType("일반도로");
                            break;
                        case 20:
                            segmentDTO.setRoadType("번화가 링크");
                            break;
                    }
                    System.out.println(semiRoute.getNumIndex());
                    if (semiRoute.getSemiRouteRoadInfo() != null)
                        segmentDTO.setTraffic(semiRoute.getSemiRouteRoadInfo().getCongestion());
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
