package DriveMate.drivemate.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ReportDTO {
    /*
    "reportId": "98765",
      "title": "김포공항-중앙대",
      "date": "2024-11-15",
      "time": "13:00-13:30",
      "distance": 15.5
     */
    private Long reportId;
    private String title;
    private String date;
    private String time;
    private Integer distance;
}
