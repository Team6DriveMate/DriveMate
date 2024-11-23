package DriveMate.drivemate.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter
public class DriveReportListRespondDTO {
    @JsonProperty("reports")
    private List<ReportDTO> reports = new ArrayList<>();

    public void addReport(ReportDTO reportDTO){
        reports.add(reportDTO);
    }
}
