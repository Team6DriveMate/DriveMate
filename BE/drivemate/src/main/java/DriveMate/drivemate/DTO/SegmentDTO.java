package DriveMate.drivemate.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter
public class SegmentDTO {
    @JsonProperty("segmentIndex")
    private Integer segmentIndex;

    @JsonProperty("distance")
    private int distance;

    @JsonProperty("time")
    private double time;

    @JsonProperty("roadName")
    private String roadName;

    @JsonProperty("traffic")
    private String traffic;

    @JsonProperty("roadType")
    private String roadType;

    private PointDTO startPoint;
    private PointDTO endPoint;
    private List<PathPointDTO> path = new ArrayList<>();

    public void addPathPointDTO(PathPointDTO pathPointDTO){
        this.path.add(pathPointDTO);
    }
}
