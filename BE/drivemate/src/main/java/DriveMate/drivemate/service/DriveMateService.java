package DriveMate.drivemate.service;
import DriveMate.drivemate.domain.Coordinate;
import DriveMate.drivemate.domain.Route;
import DriveMate.drivemate.domain.SemiRouteLineString;
import DriveMate.drivemate.domain.SemiRoutePoint;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class DriveMateService {
    private final RestTemplate restTemplate;
    private final String routeUrl = "https://apis.openapi.sk.com/tmap/routes";

    private final String trafficUrl = "https://apis.openapi.sk.com/tmap/traffic";

    @Value("${tmap.api.key}") // application.properties 파일에 API 키를 저장
    private String appKey;

    public DriveMateService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    public String getTraffic(double  centerLat, double centerLon){
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.set("appKey", appKey);

        String requestBody = String.format(
                "version=1&centerLat=%s&centerLon=%s&trafficType=POINT",
                centerLat, centerLon
        );

        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> responseEntity = restTemplate.exchange(trafficUrl, HttpMethod.GET, requestEntity, String.class);

        return responseEntity.getBody();
    }

    public String getRoute(double startY, double startX, double endY, double endX) {
        RestTemplate restTemplate = new RestTemplate(); // RESTful HTTP 통신 요청을 보내기 위해서.

        // HTTP 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("appKey", appKey);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        // 요청 본문 작성
        String requestBody = String.format(
                "{ \"startY\": %s, \"startX\": %s, \"endY\": %s, \"endX\": %s, \"tollgateFareOption\": 2, \"mainRoadInfo\": \"Y\" }",
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

    public Route parseRouteData(JsonNode responseNode) {
        Route route = new Route();

        // features 배열에서 순차적으로 경로 정보를 파싱
        JsonNode featuresArray = responseNode.get("features");

        if (featuresArray != null && featuresArray.isArray()) {
            for (JsonNode feature : featuresArray) {
                // geometry와 properties 파싱
                String type = feature.get("geometry").get("type").asText();

                JsonNode properties = feature.get("properties");
                if (type.equals("Point")) {
                    // Point 파싱 로직
                    SemiRoutePoint point = new SemiRoutePoint();

                    JsonNode coordinates = feature.get("geometry").get("coordinates");
                    // 좌표 파싱하여 Coordinate 객체에 추가
                    point.addCoordinate(parseCoordinate(coordinates));

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
                                lineString.addCoordinate(coordinate); // addCoordinate 사용
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

    // LineString에서 여러 좌표를 파싱하는 메서드
    private List<Coordinate> parseCoordinatesList(JsonNode coordinatesArray) {
        List<Coordinate> coordinates = new ArrayList<>();

        if (coordinatesArray.isArray()) {
            for (JsonNode coordinateNode : coordinatesArray) {
                Coordinate coordinate = parseCoordinate(coordinateNode);
                if (coordinate != null) {
                    coordinates.add(coordinate);
                }
            }
        }
        return coordinates;
    }

    private String getStringValue(JsonNode node, String fieldName) {
        JsonNode valueNode = node.get(fieldName);
        return (valueNode != null && !valueNode.isNull()) ? valueNode.asText() : null;
    }

    private int getIntValue(JsonNode node, String fieldName) {
        JsonNode valueNode = node.get(fieldName);
        return (valueNode != null && !valueNode.isNull()) ? valueNode.asInt() : 0;
    }


}
