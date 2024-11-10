package DriveMate.drivemate.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class AddressRequestDTO {
    @JsonProperty("road_address")
    private String address;
}
