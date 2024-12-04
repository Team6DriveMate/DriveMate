package DriveMate.drivemate.controller;

import DriveMate.drivemate.DTO.*;
import DriveMate.drivemate.domain.*;
import DriveMate.drivemate.service.DriveMateService;
import DriveMate.drivemate.service.DriveReportService;
import DriveMate.drivemate.service.RouteService;
import DriveMate.drivemate.service.UserService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/report")
public class ReportController {
    private final DriveMateService driveMateService;
    private final RouteService routeService;
    private final DriveReportService driveReportService;

    private final UserService userService;

    private Double startLat;
    private Double startLon;
    private Double endLat;
    private Double endLon;

    @Autowired
    public ReportController(DriveMateService driveMateService, RouteService routeService, UserService userService, DriveReportService driveReportService) {
        this.driveMateService = driveMateService;
        this.routeService = routeService;
        this.userService = userService;
        this.driveReportService = driveReportService;
    }


    @PostMapping("/complete")
    public SuccessRespondDTO setRoute(@RequestBody PostRouteRequestDTO postRouteRequestDTO){
        SuccessRespondDTO successRespondDTO = new SuccessRespondDTO();
        String passList = "";

        startLat = postRouteRequestDTO.getStart_location().getLat();
        startLon = postRouteRequestDTO.getStart_location().getLng();
        endLat = postRouteRequestDTO.getEnd_location().getLat();
        endLon = postRouteRequestDTO.getEnd_location().getLng();

        if (postRouteRequestDTO.getStopover_location() != null) {
            for (int i = 0; i < postRouteRequestDTO.getStopover_location().size(); i++) {
                String tmp = postRouteRequestDTO.getStopover_location().get(i).getLng().toString()
                        + "," + postRouteRequestDTO.getStopover_location().get(i).getLat().toString();
                passList = passList + tmp;
                if (i != postRouteRequestDTO.getStopover_location().size() - 1)
                    passList += "_";
            }
        }

        System.out.println(passList);
        String tmapResponse;
        System.out.println(passList);

        if (passList.equals("")){
            tmapResponse = driveMateService.getRoute(
                    postRouteRequestDTO.getStart_location().getLat(), postRouteRequestDTO.getStart_location().getLng(),
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
                for (SemiRoute semiRoute : section.getSemiRouteList()){
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

                        /*
                        0: 고속국도
                        1: 자동차전용
                        2: 국도
                        3: 국가지원 지방도
                        4: 지방도
                        5: 주요도로1(일반도로 1 중 6,5차로)
                        6: 주요도로2(일반도로 1 중 4,3차로)
                        7: 주요도로3(일반도로 1 중 2차로)
                        8: 기타도로1(일반도로 1 중 1차로)
                        9: 기타도로2(이면도로)
                        10: 페리항로
                        11: 단지 내 도로(아파트 단지 내 도로)
                        12: 단지 내 도로(시장내 도로)
                        16: 일반도로
                        20: 번화가 링크
                         */
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
        semiRouteSurvey.setRoadType(semiRouteSurveyRequestDTO.getRoadType());
        if (semiRouteSurvey.getLaneSwitch()){
            userService.getCurrentUser().getWeakPoints().replace("roadType", userService.getCurrentUser().getWeakPoints().get("roadType") + 1);
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
        Route route = driveMateService.getPostRouteTmp();
        String startLocation = driveMateService.CoordinateToAddress(startLat, startLon).replace("\"", "");
        String endLocation = driveMateService.CoordinateToAddress(endLat, endLon).replace("\"", "");
        ////////////////////////////

        LocalDateTime now = LocalDateTime.now();

        // Formatter 생성
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // LocalDateTime -> String
        String formattedDate = now.format(formatter);

        System.out.println(startLocation);
        System.out.println(endLocation);

        driveMateService.getPostRouteTmp().getDriveReport().setStartLocation(startLocation);
        driveMateService.getPostRouteTmp().getDriveReport().setEndLocation(endLocation);
        driveMateService.getPostRouteTmp().getDriveReport().setStartTime(submitRequestDTO.getStartTime());
        driveMateService.getPostRouteTmp().getDriveReport().setEndTime(formattedDate);

        User user = userService.getCurrentUser();
        user.updateExperienceByRoute();
        user.expToLevel();
        List<String> obtainedTitleList = user.updateTitle();

        SubmitRespondDTO submitRespondDTO = new SubmitRespondDTO();
        submitRespondDTO.setSuccess(true);
        if (!obtainedTitleList.isEmpty()) {
            submitRespondDTO.setTitleUpdate(true);
            for (String titleName : obtainedTitleList){
                ObtainedTitleDTO obtainedTitleDTO = new ObtainedTitleDTO();
                obtainedTitleDTO.setTitle(titleName);
                submitRespondDTO.addTitle(obtainedTitleDTO);
            }
        }
        else{
            submitRespondDTO.setTitleUpdate(false);
        }

        userService.updateUser(userService.getCurrentUser());
        driveMateService.saveRoute(driveMateService.getPostRouteTmp());

        return submitRespondDTO;
    }

    @GetMapping("/list")
    public DriveReportListRespondDTO getDriveReportList(){
        User user = userService.getCurrentUser();
        List<DriveReport> driveReportList = user.getDriveReportList();
        DriveReportListRespondDTO driveReportListRespondDTO = new DriveReportListRespondDTO();
        for (DriveReport driveReport : driveReportList){
            ReportDTO reportDTO = new ReportDTO();
            String startDateTime = driveReport.getStartTime();
            String endDateTime = driveReport.getEndTime();
            String[] splitStartDateTime = startDateTime.split("[TZ]");
            String[] splitEndDateTime = endDateTime.split("[TZ]");
            reportDTO.setReportId(driveReport.getId());
            reportDTO.setTitle(driveReport.getStartLocation() + " - " +driveReport.getEndLocation());
            reportDTO.setDistance(driveReport.getRoute().getTotalDistance()); // 여기서 문제가 생기나봐
            reportDTO.setTime(splitStartDateTime[1] + " - " + splitEndDateTime[1]);
            reportDTO.setDate(splitStartDateTime[0] + " - " + splitEndDateTime[0]);
            driveReportListRespondDTO.addReport(reportDTO);
        }
        return driveReportListRespondDTO;
    }

    @GetMapping("/{reportId}")
    public DriveReportRespondDTO getDriveReport(@PathVariable Long reportId) {
        DriveReportRespondDTO driveReportRespondDTO = new DriveReportRespondDTO();
        DriveReport driveReport = driveReportService.getDriveReportById(reportId);
        driveReportRespondDTO.setDriveId(driveReport.getId());
        driveReportRespondDTO.setStartLocation(driveReport.getStartLocation());
        driveReportRespondDTO.setEndLocation(driveReport.getEndLocation());
        driveReportRespondDTO.setStartTime(driveReport.getStartTime());
        driveReportRespondDTO.setEndTime(driveReport.getEndTime());
        driveReportRespondDTO.setDistance(driveReport.getRoute().getTotalDistance());
        driveReportRespondDTO.setTimeTaken(driveReport.getRoute().getTotalTime());

        for (SemiRouteSurvey semiRouteSurvey : driveReport.getSemiRouteSurveyList()) {
            SegmentSurveyDTO segmentSurveyDTO = new SegmentSurveyDTO();
            switch(semiRouteSurvey.getSemiRouteLineString().getRoadType()){
                case 0:
                    segmentSurveyDTO.setSegmentName("고속국도");
                    break;
                case 1:
                    segmentSurveyDTO.setSegmentName("자동차전용");
                    break;
                case 2:
                    segmentSurveyDTO.setSegmentName("국도");
                    break;
                case 3:
                    segmentSurveyDTO.setSegmentName("국가지원 지방도");
                    break;
                case 4:
                    segmentSurveyDTO.setSegmentName("지방도");
                    break;
                case 5:
                    segmentSurveyDTO.setSegmentName("5-6차선");
                    break;
                case 6:
                    segmentSurveyDTO.setSegmentName("3-4차선");
                    break;
                case 7:
                    segmentSurveyDTO.setSegmentName("2차선");
                    break;
                case 8:
                    segmentSurveyDTO.setSegmentName("1차선");
                    break;
                case 9:
                    segmentSurveyDTO.setSegmentName("이면도로");
                    break;
                case 10:
                    segmentSurveyDTO.setSegmentName("페리항로");
                    break;
                case 11:
                    segmentSurveyDTO.setSegmentName("아파트 단지 내 도로");
                    break;
                case 12:
                    segmentSurveyDTO.setSegmentName("시장 내 도로");
                    break;
                case 16:
                    segmentSurveyDTO.setSegmentName("일반도로");
                    break;
                case 20:
                    segmentSurveyDTO.setSegmentName("번화가 링크");
                    break;
            }
            segmentSurveyDTO.setTrafficCongestion(semiRouteSurvey.getTrafficCongestion());
            segmentSurveyDTO.setRoadType(semiRouteSurvey.getRoadType());
            segmentSurveyDTO.setLaneSwitch(semiRouteSurvey.getLaneSwitch());
            segmentSurveyDTO.setSituationDecision(semiRouteSurvey.getSituationDecision());
            segmentSurveyDTO.setTrafficLaws(semiRouteSurvey.getTrafficLaws());
            segmentSurveyDTO.setTensions(semiRouteSurvey.getTension());
            segmentSurveyDTO.setLaneConfusion(semiRouteSurvey.getLaneConfusion());
            segmentSurveyDTO.setSegmentIndex(semiRouteSurvey.getSemiRouteLineString().getNumIndex());
            driveReportRespondDTO.addSegment(segmentSurveyDTO);
        }

        Survey survey = driveReport.getSurvey();

        OverallSurveyDTO overallSurveyDTO = new OverallSurveyDTO();
        overallSurveyDTO.setSwitchLight(survey.getSwitchLight());
        overallSurveyDTO.setSideMirror(survey.getSideMirror());
        overallSurveyDTO.setTension(survey.getTensionLevel());
        overallSurveyDTO.setWeather(survey.getWeather());
        overallSurveyDTO.setLaneStaying(survey.getLaneStaying());
        overallSurveyDTO.setSightDegree(survey.getSightDegree());
        overallSurveyDTO.setMemo(survey.getMemo());

        driveReportRespondDTO.setOverallSurvey(overallSurveyDTO);

        driveReportRespondDTO.setSightDegree(survey.getSightDegree());
        driveReportRespondDTO.setWeakPoints(userService.getCurrentUser().getTop3WeakPoints());

        Route route = driveReport.getRoute();

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
        driveReportRespondDTO.setPath(routeResponseDTO);

        return driveReportRespondDTO;
    }

    @PostMapping("/read") // 경험치
    public SuccessRespondDTO checkReport(){
        SuccessRespondDTO successRespondDTO = new SuccessRespondDTO();
        try {
            User user = userService.getCurrentUser();
            user.updateExperienceByCheck();
            user.expToLevel();
            userService.updateUser(userService.getCurrentUser());
            successRespondDTO.setSuccess(true);
        } catch (Exception e){
            successRespondDTO.setSuccess(false);
        }
        return successRespondDTO;
    }
}
