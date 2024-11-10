package DriveMate.drivemate.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class RouteRequestDTO {
    @JsonProperty("start_location")
    private CoordinateDTO start_location;

    @JsonProperty("end_location")
    private CoordinateDTO end_location;
}
