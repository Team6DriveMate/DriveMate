package DriveMate.drivemate.DTO;

import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class RouteSurveyRequestDTO {
    private Integer switchLight;
    private Integer sideMirror;
    private Integer tension;
    private Integer weather;
    private Integer laneStaying;
    private Integer sightDegree;
    private String memo;
}
