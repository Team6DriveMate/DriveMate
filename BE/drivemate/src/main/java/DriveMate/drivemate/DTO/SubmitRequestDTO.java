package DriveMate.drivemate.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class SubmitRequestDTO {
    private String startLocation;
    private String endLocation;
    private String startTime;
    private String endTime;
}
