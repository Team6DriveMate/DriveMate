package DriveMate.drivemate.service;
import DriveMate.drivemate.domain.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;


import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.locationtech.jts.util.Debug.print;

@Service
public class DriveMateService {
    private final RestTemplate restTemplate;

    @PersistenceContext
    private EntityManager em;
    private final RouteService routeService;

    private final DriveReportService driveReportService;
    private final String routeUrl = "https://apis.openapi.sk.com/tmap/routes";
    private final String addressUrl = "https://apis.openapi.sk.com/tmap/geo/geocoding";
    private final String trafficUrl = "https://apis.openapi.sk.com/tmap/traffic";
    private final String geoURl = "https://apis.openapi.sk.com/tmap/geo/reversegeocoding";

    @Value("${tmap.api.key}") // application.properties 파일에 API 키를 저장
    private String appKey;

    @Autowired
    public DriveMateService(RestTemplateBuilder restTemplateBuilder, RouteService routeService, DriveReportService driveReportService) {
        this.restTemplate = new RestTemplate();
        this.routeService = routeService;
        this.driveReportService = driveReportService;
    }


    /**
     *  addressToCoordinate
     *  도로명주소 -> 위도 경도
     */

    public String addressToCoordinate(String address){
        String[] tokens = address.split(" ");
        // 0 : 시,도 / 1 : 구,군 / 2 : 도로명 / 3 : 번지 / 4 : 상세주소

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.set("appKey", appKey);

        for (int i = 0; i < tokens.length; i++) {
            try {
                tokens[i] = new String(tokens[i].getBytes("UTF-8"), "UTF-8");
            } catch (UnsupportedEncodingException uee) {
                uee.printStackTrace();
            }
        }

        String urlWithParams = String.format(
                "%s?version=1&city_do=%s&gu_gun=%s&dong=%s&bunji=%s&addressFlag=F00&coordType=WGS84GEO",
                addressUrl, tokens[0], tokens[1], tokens[2], tokens[3]
        );

        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

        // GET 요청으로 변경된 URL과 함께 전송
        ResponseEntity<String> responseEntity = restTemplate.exchange(urlWithParams, HttpMethod.GET, requestEntity, String.class
        );

        ObjectMapper objectMapper = new ObjectMapper();
        String newLat = null;
        String newLon = null;

        try {
            JsonNode rootNode = objectMapper.readTree(responseEntity.getBody());
            newLat = rootNode.path("coordinateInfo").path("newLat").asText();
            newLon = rootNode.path("coordinateInfo").path("newLon").asText();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return String.format("newLat: %s, newLon: %s", newLat, newLon);
    }



    /**
     *  getRoute
     *  출발 좌표와 도착 좌표를 받아 경로 데이터가 담긴 JSON 파일을 받아온다.
     */


    public String getRoute(double startY, double startX, double endY, double endX) {

        // HTTP 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("appKey", appKey);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        // 요청 본문 작성
        String requestBody = String.format(
                "{ \"startY\": %s, \"startX\": %s, \"endY\": %s, \"endX\": %s, \"tollgateFareOption\": 2, \"mainRoadInfo\": \"N\" }",
                startY, startX, endY, endX
        );

        // HttpEntity 생성. 본문과 헤더의 캡슐화.
        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

        /*
            restTemplate: Spring의 RestTemplate 객체로, RESTful 서비스와의 HTTP 통신을 쉽게 처리하기 위한 도구입니다. 주로 HTTP 요청을 보내고 응답을 처리하는 역할을 합니다.
            exchange: RestTemplate의 메서드 중 하나로, HTTP 요청을 보내고 응답을 받을 수 있는 기능을 제공합니다. exchange는 매우 유연하며, 다양한 HTTP 메서드(GET, POST, PUT, DELETE 등)를 지원합니다.
            apiUrl: 요청을 보낼 대상의 URL입니다. 이 URL은 API 엔드포인트를 나타냅니다. 예를 들어, https://example.com/api/resource와 같은 형식으로, HTTP 요청이 이 주소로 전송됩니다.
            HttpMethod.POST: 요청을 보낼 HTTP 메서드를 지정합니다. 여기서는 POST 메서드를 사용하고 있습니다. 즉, 클라이언트가 서버로 데이터를 전송할 때 사용됩니다. 다른 예로는 HttpMethod.GET, HttpMethod.PUT, HttpMethod.DELETE 등이 있습니다.
            requestEntity: HTTP 요청 본문과 헤더를 포함하는 HttpEntity 객체입니다. 이 객체는 요청에 포함될 데이터를 캡슐화하고, 헤더 정보도 포함할 수 있습니다. 위에서 예시로 HttpEntity<String>으로 구성된 요청 데이터를 보내는 부분에 대해 설명드렸습니다.
            String.class: 이 값은 응답 본문의 타입을 지정합니다. 여기서는 응답이 문자열 형식일 것이라는 것을 의미합니다. 즉, API에서 반환하는 응답이 String 형식으로 처리된다는 것을 나타냅니다. 만약 API 응답이 JSON이면, String 대신 MyResponseObject.class와 같이 특정 객체로 변환할 수도 있습니다.
        */

        // POST 요청 보내기
        ResponseEntity<String> responseEntity = restTemplate.exchange(routeUrl, HttpMethod.POST, requestEntity, String.class);

        // 응답 반환
        return responseEntity.getBody();
    }

    /**
     *  parseRouteData
     *  경로 JSON 파일을 파싱한다. 이 과정에서 Route - SemiRoute - Coordinate 값을 설정하게 된다.
     */


    public Route parseRouteData(JsonNode responseNode) {
        Route route = new Route();
        DriveReport driveReport = new DriveReport();
        route.setDriveReport(driveReport);

        // features 배열에서 순차적으로 경로 정보를 파싱
        JsonNode featuresArray = responseNode.get("features");

        boolean isFirst = true;

        if (featuresArray != null && featuresArray.isArray()) {
            for (JsonNode feature : featuresArray) {
                if (isFirst){
                    JsonNode properties = feature.get("properties");
                    route.setTotalDistance(getIntValue(properties, "totalDistance"));
                    route.setTotalTime(getIntValue(properties, "totalTime"));
                    isFirst = false;
                }
                // geometry와 properties 파싱
                String type = feature.get("geometry").get("type").asText();

                JsonNode properties = feature.get("properties");
                if (type.equals("Point")) {
                    // Point 파싱 로직
                    SemiRoutePoint point = new SemiRoutePoint();

                    JsonNode coordinates = feature.get("geometry").get("coordinates");
                    // 좌표 파싱하여 Coordinate 객체에 추가
                    Coordinate coordinate = parseCoordinate(coordinates);

                    if (coordinate != null) {
                        String trafficRespond = getTraffic(coordinate);
                        try {
                            ObjectMapper objectMapper = new ObjectMapper();
                            JsonNode jsonNode = objectMapper.readTree(trafficRespond);
                            parseTrafficInfo(jsonNode, coordinate);
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                        String geoRespond = getGeo(coordinate);
                        try {
                            ObjectMapper objectMapper = new ObjectMapper();
                            JsonNode jsonNode = objectMapper.readTree(geoRespond);
                            parseGeoInfo(jsonNode, driveReport, point);
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    point.addCoordinate(coordinate);

                    if (properties != null) {
                        point.setNumIndex(getIntValue(properties, "index"));
                        point.setPointIndex(getIntValue(properties, "pointIndex"));
                        point.setName(getStringValue(properties, "name"));
                        point.setDescription(getStringValue(properties, "description"));
                        point.setNextRoadName(getStringValue(properties, "nextRoadName"));
                        point.setTurnType(getIntValue(properties, "turnType"));
                        point.setPointType(getStringValue(properties, "pointType"));
                    }
                    point.setRoute(route);
                } else if (type.equals("LineString")) {
                    // LineString 파싱 로직
                    SemiRouteLineString lineString = new SemiRouteLineString();

                    JsonNode coordinatesArray = feature.get("geometry").get("coordinates");
                    if (coordinatesArray.isArray()) {
                        for (JsonNode coordinateNode : coordinatesArray) {
                            Coordinate coordinate = parseCoordinate(coordinateNode);
                            if (coordinate != null) {
                                coordinate.setSemiRoute(lineString);
                            }
                        }
                    }
                    if (properties != null) {
                        lineString.setNumIndex(getIntValue(properties, "index"));
                        lineString.setLineIndex(getIntValue(properties, "lineIndex"));
                        lineString.setName(getStringValue(properties, "name"));
                        lineString.setDescription(getStringValue(properties, "description"));
                        lineString.setDistance(getIntValue(properties, "distance"));
                        lineString.setTime(getIntValue(properties, "time"));
                        lineString.setRoadType(getIntValue(properties, "roadType"));
                        lineString.setFacilityType(getIntValue(properties, "facilityType"));
                    }
                    lineString.setRoute(route);
                }
            }
        }
        return route;
    }


    /**
     *  getTraffic
     *  Coordinate를 받아 POINT Traffic info를 받아온다.
     */

    public String getTraffic(Coordinate coordinate) {

        // 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.set("appKey", appKey);

        double centerLat = coordinate.getFirst();
        double centerLon = coordinate.getSecond();

        // URL에 쿼리 파라미터 포함
        String urlWithParams = String.format(
                "%s?version=1&centerLat=%f&centerLon=%f&trafficType=POINT&radius=1&zoomLevel=10",
                trafficUrl, centerLat, centerLon
        );

        // HttpEntity는 헤더만 포함 (GET 요청은 보통 바디 없이 보냄)
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

        // GET 요청으로 변경된 URL과 함께 전송
        ResponseEntity<String> responseEntity = restTemplate.exchange(urlWithParams, HttpMethod.GET, requestEntity, String.class
        );

        return responseEntity.getBody();
    }


    /**
     *  parseTrafficInfo
     *  POINT traffic info가 담긴 JSON 파일과 info와 매핑할 Coordinate를 인자로 받는다.
     *  parse된 info 객체와 coordinate 객체를 연결한다.
     */

    // 도로 정보, 돌발 정보 api 받아오기
    public Coordinate parseTrafficInfo(JsonNode responseNode, Coordinate coordinate){
        JsonNode featuresArray = responseNode.get("features");

        if (featuresArray != null && featuresArray.isArray()) {
            for (JsonNode feature : featuresArray) {
                SemiRouteRoadInfo info = new SemiRouteRoadInfo();
                JsonNode properties = feature.get("properties");
                if (properties != null){
                    info.setInfoIndex(getIntValue(properties, "index"));
                    info.setName(getStringValue(properties, "name"));
                    info.setDescription(getStringValue(properties, "description"));
                    info.setCongestion(getStringValue(properties, "congestion"));
                    info.setDirection(getStringValue(properties, "direction"));
                    info.setRoadType(getStringValue(properties, "roadType"));
                    info.setDistance(getIntValue(properties, "distance"));
                    info.setTime(getDoubleValue(properties, "time"));
                    info.setSpeed(getIntValue(properties, "speed"));
                }
                info.setCoordinate(coordinate);

            }
        }
        return coordinate;
    }


    /**
     *
     */

    public String getGeo(Coordinate coordinate){
        // 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.set("appKey", appKey);

        double Lat = coordinate.getFirst();
        double Lon = coordinate.getSecond();

        // URL에 쿼리 파라미터 포함
        String urlWithParams = String.format(
                "%s?version=1&&lat=%f&lon=%f&coordType=WGS84GEO&addressType=A02&newAddressExtend=Y",
                geoURl, Lat, Lon
        );

        // HttpEntity는 헤더만 포함 (GET 요청은 보통 바디 없이 보냄)
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

        // GET 요청으로 변경된 URL과 함께 전송
        ResponseEntity<String> responseEntity = restTemplate.exchange(urlWithParams, HttpMethod.GET, requestEntity, String.class
        );

        return responseEntity.getBody();
    }

    public Section parseGeoInfo(JsonNode jsonNode, DriveReport driveReport, SemiRoute semiRoute){
        JsonNode addressInfo = jsonNode.get("addressInfo");
        String sectionName = getStringValue(addressInfo, "city_do") + " " + getStringValue(addressInfo, "gu_gun");
        Section section = new Section();
        section.setSectionName(sectionName);
        section.addSemiRouteList(semiRoute);
        section.setDriveReport(driveReport);
        return section;
    }

    /**
     *
     */
    @Transactional
    public void saveRoute(Route route){
        try{
            int batchSize = 30;

            routeService.saveRoute(route);
            driveReportService.saveDriveReport(route.getDriveReport());

            List<Section> sectionList = route.getDriveReport().getSectionList();
            List<SemiRoute> semiRouteList = route.getSemiRouteList();
            List<Coordinate> coordinateList = new ArrayList<>();

            for (int i=0; i<sectionList.size(); i++){
                em.persist(sectionList.get(i));
                if (i % batchSize == 0 && i>0){
                    em.flush();
                    em.clear();
                }
            }
            em.flush();
            em.clear();

            for (int i=0; i<semiRouteList.size(); i++){
                coordinateList.addAll(semiRouteList.get(i).getCoordinateList());
                em.persist(semiRouteList.get(i));
                System.out.println(semiRouteList.get(i).getId());
                if (i % batchSize == 0 && i>0){
                    em.flush();
                    em.clear();
                }
            }
            em.flush();
            em.clear();

            for (int i=0; i<coordinateList.size(); i++){
                em.persist(coordinateList.get(i));
                System.out.println(coordinateList.get(i).getId() + " " + coordinateList.size());
                if(i % batchSize == 0 && i>0){
                    em.flush();
                    em.clear();
                    System.out.println("flush and clear");
                }
            }
            System.out.println("end");
            em.flush();
            em.clear();


        }catch (Exception e){
            e.printStackTrace();
        }
    }

    // 단일 좌표를 파싱하는 메서드
    private Coordinate parseCoordinate(JsonNode coordinates) {
        if (coordinates.isArray() && coordinates.size() == 2) {
            Coordinate coordinate = new Coordinate();
            coordinate.setFirst(coordinates.get(1).asDouble()); // 위도
            coordinate.setSecond(coordinates.get(0).asDouble()); // 경도
            return coordinate;
        }
        return null; // 유효하지 않은 좌표의 경우 null 반환
    }

    private String getStringValue(JsonNode node, String fieldName) {
        JsonNode valueNode = node.get(fieldName);
        return (valueNode != null && !valueNode.isNull()) ? valueNode.asText() : null;
    }

    private int getIntValue(JsonNode node, String fieldName) {
        JsonNode valueNode = node.get(fieldName);
        return (valueNode != null && !valueNode.isNull()) ? valueNode.asInt() : 0;
    }

    private double getDoubleValue(JsonNode node, String fieldName) {
        JsonNode valueNode = node.get(fieldName);
        return (valueNode != null && !valueNode.isNull()) ? valueNode.asDouble() : 0;
    }


}
