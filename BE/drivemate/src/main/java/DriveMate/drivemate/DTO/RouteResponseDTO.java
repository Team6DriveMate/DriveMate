package DriveMate.drivemate.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class RouteResponseDTO {
    @JsonProperty("totalDistance")
    private Integer totalDistance;

    @JsonProperty("totalTime")
    private Integer totalTime;

    @JsonProperty("route")
    private RouteDTO route;
}
