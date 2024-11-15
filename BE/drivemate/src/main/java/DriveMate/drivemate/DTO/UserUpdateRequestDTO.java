package DriveMate.drivemate.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UserUpdateRequestDTO {
    @JsonProperty("nickname")
    public String nickname;

    @JsonProperty("mainTitle")
    public String mainTitle;
}
