package DriveMate.drivemate.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class SuccessRespondDTO {
    @JsonProperty("success")
    private boolean success;

}
