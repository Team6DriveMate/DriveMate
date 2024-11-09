package DriveMate.drivemate.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class PathPointDTO {
    @JsonProperty("lat")
    private double lat;

    @JsonProperty("lng")
    private double lng;
}
