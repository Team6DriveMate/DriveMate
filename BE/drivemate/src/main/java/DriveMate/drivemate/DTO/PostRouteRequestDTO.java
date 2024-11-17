package DriveMate.drivemate.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter
public class PostRouteRequestDTO {
    @JsonProperty("start_location")
    private PointDTO start_location;

    @JsonProperty("end_location")
    private PointDTO end_location;

    @JsonProperty("stopover_location")
    private List<PointDTO> stopover_location = new ArrayList<>();
}
