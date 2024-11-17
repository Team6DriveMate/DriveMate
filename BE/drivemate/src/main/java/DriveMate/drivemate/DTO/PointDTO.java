package DriveMate.drivemate.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class PointDTO {
    @JsonProperty("lat")
    private Double lat;

    @JsonProperty("lng")
    private Double lng;
}
