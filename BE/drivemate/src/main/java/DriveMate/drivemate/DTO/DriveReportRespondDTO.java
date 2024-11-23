package DriveMate.drivemate.DTO;

import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter
public class DriveReportRespondDTO {
    private Long driveId;
    private String startLocation;
    private String endLocation;
    private String startTime;
    private String endTime;
    private Integer distance;
    private Integer timeTaken; // 운행 전 예상 소요 시간
    private List<SegmentSurveyDTO> segmentSurveys = new ArrayList<>();
    private OverallSurveyDTO overallSurvey;
    private Integer sightDegree;
    private List<String> weakPoints = new ArrayList<>();

    public void addSegment(SegmentSurveyDTO segmentSurveyDTO){
        segmentSurveys.add(segmentSurveyDTO);
    }
    /*
    "driveId": "(운행 ID)",
    "startLocation": "(출발지)",
    "endLocation": "(도착지)",
    "startTime": "2024-11-15T13:00:00Z",
    "endTime": "2024-11-15T13:30:00Z",
    "distance": "15.5",
    "timeTaken": "30 mins",
    "sectionSurveys": [
      {
        "sectionName": "(구간 1 이름)",
        "trafficCongestion": true,
        "roadType": "(도로 유형)",
        "laneChange": false,
        "judgmentLack": true,
        "laneConfusion": false,
        "trafficRuleCompliance": true,
        "tension": false
      },
      {
        "sectionName": "(구간 2 이름)",
        "trafficCongestion": false,
        "roadType": "(도로 유형)",
        "laneChange": true,
        "judgmentLack": false,
        "laneConfusion": true,
        "trafficRuleCompliance": false,
        "tension": true
      }
      // 구간별 설문 추가 가능
    ],
    "overallSurvey": {
      "question1": {
        "rating": "4"
      },
      "question2": {
        "rating": "3"
      },
      "question3": {
        "rating": "5"
      },
      "question4": {
        "rating": "2"
      },
      "question5": {
        "rating": "4"
      },
      "memo": "(전체 메모)"
    },
    "fieldOfView": "(시야각)",
    "weakPoints": ["(취약점1)", "(취약점2)", "(취약점3)"]
  }
     */
}
