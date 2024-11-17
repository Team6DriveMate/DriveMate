package DriveMate.drivemate.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class PostRouteRespondDTO {
    @JsonProperty("totalDistance")
    private Integer totalDistance;

    @JsonProperty("totalTime")
    private Integer totalTime;

    @JsonProperty("route")
    private PostRouteDTO route;
}
