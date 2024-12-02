package DriveMate.drivemate.DTO;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter
public class SubmitRespondDTO {
    private Boolean success;
    private Boolean titleUpdate;
    private List<ObtainedTitleDTO> titles = new ArrayList<>();

    public void addTitle(ObtainedTitleDTO obtainedTitleDTO){
        titles.add(obtainedTitleDTO);
    }
}
