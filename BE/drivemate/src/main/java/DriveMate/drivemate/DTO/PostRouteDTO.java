package DriveMate.drivemate.DTO;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter
public class PostRouteDTO {
    private List<SectionDTO> sections = new ArrayList<>();

    public void addSection(SectionDTO sectionDTO){
        this.sections.add(sectionDTO);
    }
}
