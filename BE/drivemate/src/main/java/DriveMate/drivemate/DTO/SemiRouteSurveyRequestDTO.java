package DriveMate.drivemate.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class SemiRouteSurveyRequestDTO {
    @JsonProperty("sectionName")
    private String sectionName;

    @JsonProperty("trafficCongestion")
    private Boolean trafficCongestion;

    @JsonProperty("roadType")
    private Boolean roadType;

    @JsonProperty("laneSwitch")
    private Boolean laneSwitch;

    @JsonProperty("situationDecision")
    private Boolean situationDecision;

    @JsonProperty("laneConfusion")
    private Boolean laneConfusion;

    @JsonProperty("trafficLaws")
    private Boolean trafficLaws;

    @JsonProperty("tension")
    private Boolean tension;
}
