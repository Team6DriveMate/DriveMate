package DriveMate.drivemate.DTO;

import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter
public class DriveReportRespondDTO {
    private Long driveId;
    private String startLocation;
    private String endLocation;
    private String startTime;
    private String endTime;
    private Integer distance;
    private Integer timeTaken; // 운행 전 예상 소요 시간
    private List<SegmentSurveyDTO> segmentSurveys = new ArrayList<>();
    private OverallSurveyDTO overallSurvey;
    private Integer sightDegree;
    private List<String> weakPoints = new ArrayList<>();
    private RouteResponseDTO path;

    public void addSegment(SegmentSurveyDTO segmentSurveyDTO){
        segmentSurveys.add(segmentSurveyDTO);
    }
}
