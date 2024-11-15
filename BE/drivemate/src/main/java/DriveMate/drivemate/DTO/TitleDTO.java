package DriveMate.drivemate.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class TitleDTO {
    @JsonProperty("name")
    private String name;

    @JsonProperty("dateObtained")
    private String dateObtained;
}
