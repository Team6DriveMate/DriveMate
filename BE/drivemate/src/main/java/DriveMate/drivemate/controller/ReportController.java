package DriveMate.drivemate.controller;

import DriveMate.drivemate.DTO.*;
import DriveMate.drivemate.domain.*;
import DriveMate.drivemate.service.DriveMateService;
import DriveMate.drivemate.service.RouteService;
import DriveMate.drivemate.service.UserService;
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

    private final UserService userService;

    @Autowired
    public ReportController(DriveMateService driveMateService, RouteService routeService, UserService userService) {
        this.driveMateService = driveMateService;
        this.routeService = routeService;
        this.userService = userService;
    }


    @PostMapping("/complete")
    public SuccessRespondDTO setRoute(@RequestBody PostRouteRequestDTO postRouteRequestDTO){
        SuccessRespondDTO successRespondDTO = new SuccessRespondDTO();
        String passList = "";

        if (postRouteRequestDTO.getStopover_location() != null) {
            for (int i = 0; i < postRouteRequestDTO.getStopover_location().size(); i++) {
                String tmp = postRouteRequestDTO.getStopover_location().get(i).getLng().toString()
                        + "," + postRouteRequestDTO.getStopover_location().get(i).getLat().toString();
                passList = passList + tmp;
                if (i != postRouteRequestDTO.getStopover_location().size() - 1)
                    passList += "_";
            }
        }

        String tmapResponse;

        if (passList.equals("")){
            tmapResponse = driveMateService.getRoute(postRouteRequestDTO.getStart_location().getLat(), postRouteRequestDTO.getStart_location().getLng(),
                    postRouteRequestDTO.getEnd_location().getLat(), postRouteRequestDTO.getEnd_location().getLng());
        }
        else {
            tmapResponse = driveMateService.getRouteWithPassList(
                    postRouteRequestDTO.getStart_location().getLat(), postRouteRequestDTO.getStart_location().getLng(),
                    postRouteRequestDTO.getEnd_location().getLat(), postRouteRequestDTO.getEnd_location().getLng(),
                    passList
            );
        }

        System.out.println(tmapResponse);

        try {
            // ObjectMapper를 사용하여 String을 JsonNode로 변환
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(tmapResponse);
            // 변환된 JsonNode를 parseRouteData에 전달
            Route route = driveMateService.parseRouteData(jsonNode);
            User user = userService.getCurrentUser();
            route.getDriveReport().setUser(user);
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

            int i = 0;
            PostRouteDTO postRouteDTO = new PostRouteDTO();
            for (Section section : route.getDriveReport().getSectionList()) {
                SectionDTO sectionDTO = new SectionDTO();
                sectionDTO.setSectionIndex(i++);
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
        if (semiRouteSurvey.getTension()){
            userService.getCurrentUser().getWeakPoints().replace("tension", userService.getCurrentUser().getWeakPoints().get("tension") + 1);
        }
        semiRouteSurvey.setTrafficCongestion(semiRouteSurveyRequestDTO.getTrafficCongestion());
        if (semiRouteSurvey.getTrafficCongestion()){
            userService.getCurrentUser().getWeakPoints().replace("trafficCongestion", userService.getCurrentUser().getWeakPoints().get("trafficCongestion") + 1);
        }
        semiRouteSurvey.setLaneConfusion(semiRouteSurveyRequestDTO.getLaneConfusion());
        if (semiRouteSurvey.getLaneConfusion()){
            userService.getCurrentUser().getWeakPoints().replace("laneConfusion", userService.getCurrentUser().getWeakPoints().get("laneConfusion") + 1);
        }
        semiRouteSurvey.setSituationDecision(semiRouteSurveyRequestDTO.getSituationDecision());
        if (semiRouteSurvey.getSituationDecision()){
            userService.getCurrentUser().getWeakPoints().replace("situationDecision", userService.getCurrentUser().getWeakPoints().get("situationDecision") + 1);
        }
        semiRouteSurvey.setTrafficLaws(semiRouteSurveyRequestDTO.getTrafficLaws());
        if (semiRouteSurvey.getTrafficLaws()){
            userService.getCurrentUser().getWeakPoints().replace("trafficLaws", userService.getCurrentUser().getWeakPoints().get("trafficLaws") + 1);
        }
        semiRouteSurvey.setLaneSwitch(semiRouteSurveyRequestDTO.getLaneSwitch());
        if (semiRouteSurvey.getLaneSwitch()){
            userService.getCurrentUser().getWeakPoints().replace("laneSwitch", userService.getCurrentUser().getWeakPoints().get("laneSwitch") + 1);
        }
        semiRouteSurvey.setDriveReport(driveMateService.getPostRouteTmp().getDriveReport());

        System.out.println("\n\n\n\n\n\n");
        System.out.println(driveMateService.getPostRouteTmp().getDriveReport().getSemiRouteSurveyList().size());
        System.out.println("\n\n\n\n\n\n");

        SuccessRespondDTO successRespondDTO = new SuccessRespondDTO();
        successRespondDTO.setSuccess(true);
        return successRespondDTO;
    }

    @PostMapping("/survey/route")
    public SuccessRespondDTO setRouteSurvey(@RequestBody RouteSurveyRequestDTO routeSurveyRequestDTO){
        Survey survey = new Survey();
        survey.setLaneStaying(routeSurveyRequestDTO.getLaneStaying());
        survey.setMemo(routeSurveyRequestDTO.getMemo());
        survey.setWeather(routeSurveyRequestDTO.getWeather());
        survey.setSightDegree(routeSurveyRequestDTO.getSightDegree());
        survey.setSwitchLight(routeSurveyRequestDTO.getSwitchLight());
        survey.setTensionLevel(routeSurveyRequestDTO.getTension());
        survey.setSideMirror(routeSurveyRequestDTO.getSideMirror());

        survey.setDriveReport(driveMateService.getPostRouteTmp().getDriveReport());
        SuccessRespondDTO successRespondDTO = new SuccessRespondDTO();
        successRespondDTO.setSuccess(true);
        return successRespondDTO;
    }

    @PostMapping("/submit")
    public SubmitRespondDTO submitReport(@RequestBody SubmitRequestDTO submitRequestDTO){
        driveMateService.getPostRouteTmp().getDriveReport().setStartLocation(submitRequestDTO.getStartLocation());
        driveMateService.getPostRouteTmp().getDriveReport().setEndLocation(submitRequestDTO.getEndLocation());
        driveMateService.getPostRouteTmp().getDriveReport().setStartTime(submitRequestDTO.getStartTime());
        driveMateService.getPostRouteTmp().getDriveReport().setEndTime(submitRequestDTO.getEndTime());

        userService.updateUser(userService.getCurrentUser());
        driveMateService.saveRoute(driveMateService.getPostRouteTmp());

        SubmitRespondDTO submitRespondDTO = new SubmitRespondDTO();
        submitRespondDTO.setSuccess(true);
        return submitRespondDTO;
    }

}
