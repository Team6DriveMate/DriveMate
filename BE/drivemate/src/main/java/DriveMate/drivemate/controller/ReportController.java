package DriveMate.drivemate.controller;

import DriveMate.drivemate.DTO.*;
import DriveMate.drivemate.domain.*;
import DriveMate.drivemate.service.DriveMateService;
import DriveMate.drivemate.service.RouteService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/report")
public class ReportController {
    private final DriveMateService driveMateService;
    private final RouteService routeService;

    @Autowired
    public ReportController(DriveMateService driveMateService, RouteService routeService) {
        this.driveMateService = driveMateService;
        this.routeService = routeService;
    }


    @PostMapping("/complete")
    public SuccessRespondDTO setRoute(@RequestBody PostRouteRequestDTO postRouteRequestDTO){
        SuccessRespondDTO successRespondDTO = new SuccessRespondDTO();
        String passList = "";
        for (int i=0; i<postRouteRequestDTO.getStopover_location().size(); i++){
            String tmp = postRouteRequestDTO.getStopover_location().get(i).getLng().toString()
                    + "," + postRouteRequestDTO.getStopover_location().get(i).getLat().toString();
            passList = passList + tmp;
            if (i != postRouteRequestDTO.getStopover_location().size()-1)
                passList += "_";
        }

        String tmapResponse = driveMateService.getRouteWithPassList(
                postRouteRequestDTO.getStart_location().getLat(), postRouteRequestDTO.getStart_location().getLng(),
                postRouteRequestDTO.getEnd_location().getLat(), postRouteRequestDTO.getEnd_location().getLng(),
                passList
        );

        System.out.println(tmapResponse);

        try {
            // ObjectMapper를 사용하여 String을 JsonNode로 변환
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(tmapResponse);
            // 변환된 JsonNode를 parseRouteData에 전달
            Route route = driveMateService.parseRouteData(jsonNode);
            driveMateService.setPostRouteTmp(route);

            successRespondDTO.setSuccess(true);
            return successRespondDTO;
        } catch (Exception e) {
            // 오류가 발생하면 에러 처리
            e.printStackTrace();
            successRespondDTO.setSuccess(false);
            return successRespondDTO;
        }
    }

    @GetMapping("/route")
    public PostRouteRespondDTO getRoute(){
        if (driveMateService.getPostRouteTmp() != null){
            Route route = driveMateService.getPostRouteTmp();
            PostRouteRespondDTO postRouteRespondDTO = new PostRouteRespondDTO();
            postRouteRespondDTO.setTotalTime(route.getTotalTime());
            postRouteRespondDTO.setTotalDistance(route.getTotalDistance());

            PostRouteDTO postRouteDTO = new PostRouteDTO();
            for (Section section : route.getDriveReport().getSectionList()) {
                SectionDTO sectionDTO = new SectionDTO();
                sectionDTO.setSectionName(section.getSectionName());
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
                        segmentDTO.setSegmentIndex(((SemiRouteLineString) semiRoute).getNumIndex());
                        System.out.println(semiRoute.getNumIndex());
                        if (semiRoute.getSemiRouteRoadInfo() != null)
                            segmentDTO.setTraffic(semiRoute.getSemiRouteRoadInfo().getCongestion());
                        sectionDTO.addSegment(segmentDTO);
                    }
                }
                postRouteDTO.addSection(sectionDTO);
            }
            postRouteRespondDTO.setRoute(postRouteDTO);
            return postRouteRespondDTO;
        }
        else
            return null;
    }

    @PostMapping("/survey/{segmentIndex}")
    public SuccessRespondDTO setSemiRouteSurvey(@PathVariable Integer segmentIndex, @RequestBody SemiRouteSurveyRequestDTO semiRouteSurveyRequestDTO){
        SemiRouteSurvey semiRouteSurvey = new SemiRouteSurvey();
        semiRouteSurvey.setSemiRouteLineString(
                (SemiRouteLineString) driveMateService.getPostRouteTmp().getSemiRouteList().get(segmentIndex)
        );
        semiRouteSurvey.setTension(semiRouteSurveyRequestDTO.getTension());
        semiRouteSurvey.setLaneConfusion(semiRouteSurveyRequestDTO.getLaneConfusion());
        semiRouteSurvey.setSituationDecision(semiRouteSurveyRequestDTO.getSituationDecision());
        semiRouteSurvey.setTrafficLaws(semiRouteSurveyRequestDTO.getTrafficLaws());
        semiRouteSurvey.setLaneSwitch(semiRouteSurveyRequestDTO.getLaneSwitch());
        semiRouteSurvey.setDriveReport(driveMateService.getPostRouteTmp().getDriveReport());

        SuccessRespondDTO successRespondDTO = new SuccessRespondDTO();
        successRespondDTO.setSuccess(true);
        return successRespondDTO;
    }


}
