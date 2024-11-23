package DriveMate.drivemate.DTO;

import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class OverallSurveyDTO {
    /*
    "overallSurvey": {
      "switchLight": (1-5 점수),
  "sideMirror": (1-5 점수)
  "tension": (1-5 점수),
  "weather": (1-5 점수),
  "laneStaying": (1-5 점수),
  "sightDegree": 시야각 // 정수로 주세요
  "memo": "(한줄 메모)"
    },
     */
    private Integer switchLight;
    private Integer sideMirror;
    private Integer tension;
    private Integer weather;
    private Integer laneStaying;
    private Integer sightDegree;
    private String memo;
}
