package DriveMate.drivemate.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class SignUpDTO {
    @JsonProperty("nickname")
    private String userNickname;

    @JsonProperty("username")
    private String userName;

    @JsonProperty("pw")
    private String userPW;

    @JsonProperty("confirmPw")
    private String confirmUserPW;
}
