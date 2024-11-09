package DriveMate.drivemate.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class RouteRequestDTO {
    @JsonProperty("start_location")
    private LocationDTO startLocation;

    @JsonProperty("end_location")
    private LocationDTO endLocation;
}
