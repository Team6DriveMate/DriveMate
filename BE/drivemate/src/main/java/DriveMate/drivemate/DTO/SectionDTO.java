package DriveMate.drivemate.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter
public class SectionDTO {
    @JsonProperty("sectionName")
    private String sectionName;

    @JsonProperty("segments")
    private List<SegmentDTO> segments = new ArrayList<>();

    @JsonProperty("sectionIndex")
    private Integer sectionIndex;

    public void addSegment(SegmentDTO segmentDTO){
        this.segments.add(segmentDTO);
    }
}
