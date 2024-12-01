package DriveMate.drivemate.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class SegmentSurveyDTO {
    private String segmentName;
    private Boolean trafficCongestion;
    private Boolean roadType;
    private Boolean laneSwitch;
    private Boolean situationDecision;
    private Boolean trafficLaws;
    private Boolean tensions;
    private Boolean laneConfusion;
    private Integer segmentIndex;
}
