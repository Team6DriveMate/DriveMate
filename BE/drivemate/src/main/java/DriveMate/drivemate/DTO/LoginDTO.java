package DriveMate.drivemate.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class LoginDTO {
    @JsonProperty("id")
    private String userName;

    @JsonProperty("pw")
    private String password;

    public LoginDTO(){}

    public LoginDTO(String userName, String password){
        this.userName = userName;
        this.password = password;
    }
}
